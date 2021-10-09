package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.generator.FileGenerator
import com.casadetasha.kexp.sproute.processor.ktx.*
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent.SprouteClass
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent.SproutePackage
import com.casadetasha.kexp.sproute.processor.models.SprouteRootInfo
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException

@AutoService(Processor::class)
@SupportedOptions(RouteAnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class RouteAnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private lateinit var roundEnv: RoundEnvironment
    private val routeRoots: ImmutableMap<TypeName, SprouteRootInfo> by lazy { roundEnv.getSprouteRoots() }
    private val routeClasses: ImmutableSet<SprouteClass> by lazy { roundEnv.getRouteClasses() }
    private val routePackages: ImmutableSet<SproutePackage> by lazy { roundEnv.getRoutePackages() }

    private val sortedSprouteKotlinParents: ImmutableSet<SprouteKotlinParent> by lazy {
        val mergedSortedSets = routeClasses.toSortedSet() + routePackages.toSortedSet()
        mergedSortedSets.toImmutableSet()
    }

    private lateinit var kaptKotlinGeneratedDir: String

    override fun getSupportedAnnotationTypes() = mutableSetOf(
        Sproute::class.java.canonicalName,
        SprouteRoot::class.java.canonicalName,
        Get::class.java.canonicalName
    )

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return false
        if (roundEnv == null) return false

        this.roundEnv = roundEnv
        // TODO: Investigate why process started running again without any @Route classes
        if (sortedSprouteKotlinParents.isNotEmpty()) {
            FileGenerator(processingEnv, kaptKotlinGeneratedDir).generateRouteFiles((routeClasses + routePackages).toImmutableSet())
        }
        return true
    }

    // asTypeName() should be safe since custom routes will never be Kotlin core classes
    @OptIn(DelicateKotlinPoetApi::class)
    private fun Sproute.getRootTypeName(): TypeName {
        return try {
            ClassName(sprouteRoot.java.packageName, sprouteRoot.java.simpleName)
        } catch (exception: MirroredTypeException) {
            exception.typeMirror.asTypeName()
        }
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    private fun RoundEnvironment.getRoutePackages(): ImmutableSet<SproutePackage> {
        val functionListMap = HashMap<String, MutableList<Element>>()
        val functionFileElementMap = HashMap<String, Element>()
        val functions = getElementsAnnotatedWithAny(RequestAnnotations.validRequestTypes.map { it.java }.toSet())
        val orphanFunctions = functions.filter { it.isOrphanFunction() }
        orphanFunctions.forEach {
            val key = it.enclosingElement.getQualifiedName(processingEnv)
            functionListMap.getOrCreateList(key).add(it)
            functionFileElementMap[key] = functionFileElementMap[key] ?: it.enclosingElement
        }

        return functionListMap.map {
            SproutePackage(
                processingEnv = processingEnv,
                immutableKmPackage = it.value.first().getParentFileKmPackage(),
                packageElement = processingEnv.elementUtils.getPackageOf(functionFileElementMap[it.key]),
                fileName = functionFileElementMap[it.key]?.simpleName?.toString() ?: "",
                methodElementSet = it.value.toMap()
            )
        }.toImmutableSet()
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    private fun createRouteClass(routeClassElement: Element): SprouteClass {
        val classSprouteAnnotation: Sproute = routeClassElement.getAnnotation(Sproute::class.java)
        val routeRootTypeName: TypeName = classSprouteAnnotation.getRootTypeName()
        val routeRoot = routeRoots[routeRootTypeName] ?: logAndThrowInvalidRouteTypeError(routeRootTypeName)

        return SprouteClass(
            processingEnv = processingEnv,
            classData = routeClassElement.getClassData(processingEnv),
            sprouteRootInfo = routeRoot,
            classRouteSegment = classSprouteAnnotation.routeSegment,
            classElement = routeClassElement
        )
    }

    private fun RoundEnvironment.getRouteClasses(): ImmutableSet<SprouteClass> =
        getElementsAnnotatedWith(Sproute::class.java)
            .mapToImmutableSet { createRouteClass(it) }

    private fun logAndThrowInvalidRouteTypeError(routeRootTypeName: TypeName): Nothing {
        processingEnv.printThenThrowError(
            "@SprouteRoot annotation was not found for provided class $routeRootTypeName"
        )
    }
}
