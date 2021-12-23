package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.annotationparser.AnnotationParser
import com.casadetasha.kexp.annotationparser.AnnotationParser.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.casadetasha.kexp.annotationparser.AnnotationParser.getClassesAnnotatedWith
import com.casadetasha.kexp.annotationparser.AnnotationParser.getFileFacadesForTopLevelFunctionsAnnotatedWith
import com.casadetasha.kexp.annotationparser.AnnotationParser.kaptKotlinGeneratedDir
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SproutePackageRoot
import com.casadetasha.kexp.sproute.processor.annotation_bridge.SprouteRequestAnnotationBridge
import com.casadetasha.kexp.sproute.processor.generator.FileGenerator
import com.casadetasha.kexp.sproute.processor.generator.tree.SprouteTree
import com.casadetasha.kexp.sproute.processor.ktx.getSprouteRootKey
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteClass
import com.casadetasha.kexp.sproute.processor.sproutes.SproutePackage
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteParent
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.AuthLazyLoader
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.BaseAuthentication
import com.casadetasha.kexp.sproute.processor.sproutes.segments.LeadingRouteSegment
import com.casadetasha.kexp.sproute.processor.sproutes.segments.ProcessedRouteSegments
import com.casadetasha.kexp.sproute.processor.sproutes.segments.RouteSegment
import com.casadetasha.kexp.sproute.processor.sproutes.segments.TrailingRouteSegment
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.toRequestParamMemberNames
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
class SprouteProcessor : AbstractProcessor() {

    private val sprouteClasses: Set<SprouteClass> by lazy { getRouteClasses() }
    private val sproutePackages: Set<SproutePackage> by lazy { getRoutePackages() }

    private val mergedKotlinParents: Set<SprouteParent> by lazy {
        sprouteClasses.toSortedSet() + sproutePackages.toSortedSet()
    }

    override fun getSupportedAnnotationTypes() = mutableSetOf(
        Sproute::class.java.canonicalName,
        SproutePackageRoot::class.java.canonicalName
    ) + SprouteRequestAnnotationBridge.validRequestTypes.map { it.java.canonicalName }.toMutableList()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        try {
            AnnotationParser.setup(processingEnv, roundEnv)
        } catch (_: IllegalStateException) {
            return false
        }

        ProcessedRouteSegments.putAll(getSprouteRoots())
        generateSproutes()
        return true
    }

    private fun generateSproutes() {
        // TODO: Investigate why process started running again without any annotated classes
        if (mergedKotlinParents.isEmpty()) return

        val sprouteTree: SprouteTree = SprouteTree.LazyLoader(mergedKotlinParents).value

        FileGenerator(
            kaptKotlinGeneratedDir = kaptKotlinGeneratedDir,
            sprouteTree = sprouteTree
        ).generateSproutes()
    }

    private fun getSprouteRoots(): Map<TypeName, RouteSegment> =
        HashMap<TypeName, RouteSegment>().apply {
            getClassesAnnotatedWith(SproutePackageRoot::class).forEach {
                val className = it.className
                val annotation = it.element.getAnnotation(SproutePackageRoot::class.java)!!

                this[className] = LeadingRouteSegment.AnnotatedRouteSegment(
                    segmentKey = it.className,
                    packageName = className.packageName,
                    routeSegment = annotation.rootSprouteSegment,
                    authentication = BaseAuthentication().createChildFromElement(it.element)
                )
            }
        }.toMap()


    @OptIn(KotlinPoetMetadataPreview::class)
    private fun getRoutePackages(): Set<SproutePackage> {
        return getFileFacadesForTopLevelFunctionsAnnotatedWith(SprouteRequestAnnotationBridge.validRequestTypes)
            .map {
                SproutePackage(
                    packageName = it.packageName,
                    fileName = it.fileName,
                    functions = it.getFunctionsAnnotatedWith(
                        *SprouteRequestAnnotationBridge.validRequestTypes.toTypedArray())
                )
            }
            .toSet()
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    private fun getRouteClasses(): Set<SprouteClass> =
        getClassesAnnotatedWith(Sproute::class)
            .map {
                val sprouteAnnotation: Sproute = it.element.getAnnotation(Sproute::class.java)
                val sprouteRootKey = sprouteAnnotation.getSprouteRootKey()

                val segment = TrailingRouteSegment(
                    routeSegment = sprouteAnnotation.routeSegment,
                    parentSegmentKey = sprouteRootKey,
                    segmentKey = it.className,
                    authLazyLoader = AuthLazyLoader(sprouteRootKey, it.element)
                ).apply { ProcessedRouteSegments.put(this) }

                SprouteClass(
                    classData = it.classData,
                    primaryConstructorParams = it.primaryConstructorParams?.toRequestParamMemberNames(),
                    functions = it.getFunctionsAnnotatedWith(*SprouteRequestAnnotationBridge.validRequestTypes.toTypedArray()),
                    routeSegment = segment
                )
            }.toSet()
}
