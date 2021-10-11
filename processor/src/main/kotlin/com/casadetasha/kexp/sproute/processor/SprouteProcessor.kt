package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.generator.FileGenerator
import com.casadetasha.kexp.sproute.processor.ktx.*
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent.SprouteClass
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent.SproutePackage
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedOptions(SprouteProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class SprouteProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        lateinit var processingEnvironment: ProcessingEnvironment
    }

    private lateinit var roundEnv: RoundEnvironment
    private val sprouteClasses: ImmutableSet<SprouteClass> by lazy { roundEnv.getRouteClasses() }
    private val sproutePackages: ImmutableSet<SproutePackage> by lazy { roundEnv.getRoutePackages() }

    private val sortedSprouteKotlinParents: ImmutableSet<SprouteKotlinParent> by lazy {
        val mergedSortedSets = sprouteClasses.toSortedSet() + sproutePackages.toSortedSet()
        mergedSortedSets.toImmutableSet()
    }

    private lateinit var kaptKotlinGeneratedDir: String

    override fun getSupportedAnnotationTypes() = mutableSetOf(
        Sproute::class.java.canonicalName,
        SprouteRoot::class.java.canonicalName
    ) + SprouteRequestAnnotations.validRequestTypes.map { it.java.canonicalName }.toMutableList()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        processingEnvironment = processingEnv
        kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return false

        if (roundEnv == null) return false
        SprouteRoots.putAll(roundEnv.getSprouteRoots())

        this.roundEnv = roundEnv
        // TODO: Investigate why process started running again without any @Route classes
        if (sortedSprouteKotlinParents.isNotEmpty()) {
            FileGenerator(kaptKotlinGeneratedDir).generateRouteFiles(sortedSprouteKotlinParents)
        }
        return true
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    private fun RoundEnvironment.getRoutePackages(): ImmutableSet<SproutePackage> {
        val functionListMap = HashMap<String, MutableList<Element>>()
        val functionFileElementMap = HashMap<String, Element>()
        getElementsAnnotatedWithAny(SprouteRequestAnnotations.validRequestTypes.map { it.java }.toSet())
            .filter { it.isOrphanFunction() }
            .forEach {
                val key = it.enclosingElement.getQualifiedName()
                functionListMap.getOrCreateList(key).add(it)
                functionFileElementMap[key] = functionFileElementMap[key] ?: it.enclosingElement
            }

        return functionListMap.map {
            val fileElement = functionFileElementMap[it.key]!!
            SproutePackage(
                immutableKmPackage = it.value.first().getParentFileKmPackage(),
                packageName = fileElement.packageName,
                fileName = fileElement.simpleName?.toString() ?: "",
                requestMethodMap = it.value.toMap()
            )
        }.toImmutableSet()
    }

    private fun RoundEnvironment.getRouteClasses(): ImmutableSet<SprouteClass> =
        getElementsAnnotatedWith(Sproute::class.java)
            .filterNot { it.isOrphanFunction() }
            .mapToImmutableSet { createRouteClass(it) }

    @OptIn(KotlinPoetMetadataPreview::class)
    private fun createRouteClass(routeClassElement: Element): SprouteClass {
        val classSprouteAnnotation: Sproute = routeClassElement.getAnnotation(Sproute::class.java)
        val routeRoot = classSprouteAnnotation.getSprouteRoot()
        val classData = routeClassElement.getClassData()

        return SprouteClass(
            classData = classData,
            rootPathSegment = routeRoot.getPathPrefixToSproutePackage(classData.className.packageName),
            classRouteSegment = classSprouteAnnotation.routeSegment,
            requestMethodMap = routeClassElement.getRequestMethods()
        )
    }
}
