package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.annotationparser.AnnotationParser
import com.casadetasha.kexp.annotationparser.AnnotationParser.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.casadetasha.kexp.annotationparser.AnnotationParser.kaptKotlinGeneratedDir
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SproutePackageRoot
import com.casadetasha.kexp.sproute.processor.annotation_bridge.SprouteRequestAnnotationBridge
import com.casadetasha.kexp.sproute.processor.generator.FileGenerator
import com.casadetasha.kexp.sproute.processor.generator.tree.SprouteTree
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteParent
import com.casadetasha.kexp.sproute.processor.sproutes.segments.ProcessedRouteSegments
import com.casadetasha.kexp.sproute.processor.sproutes.segments.RouteSegment
import com.casadetasha.kexp.sproute.processor.values.SprouteKotlinInfo
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
class SprouteProcessor : AbstractProcessor() {

    private val sprouteKotlinInfo by lazy { SprouteKotlinInfo.load() }
    private val mergedKotlinParents: Set<SprouteParent> by lazy { sprouteKotlinInfo.sprouteParents }
    private val sproutePackageRoots: Map<TypeName, RouteSegment> by lazy { sprouteKotlinInfo.sproutePackageRoots }

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

        ProcessedRouteSegments.putAll(sproutePackageRoots)
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
}
