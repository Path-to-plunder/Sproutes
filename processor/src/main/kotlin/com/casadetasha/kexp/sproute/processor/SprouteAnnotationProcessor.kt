package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.annotationparser.AnnotationParser
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.generator.FileGenerator
import com.casadetasha.kexp.sproute.processor.ktx.getRouteClasses
import com.casadetasha.kexp.sproute.processor.ktx.getRoutePackages
import com.casadetasha.kexp.sproute.processor.ktx.getSprouteRoots
import com.casadetasha.kexp.sproute.processor.models.Root.Companion.sprouteRoots
import com.casadetasha.kexp.sproute.processor.models.SprouteTree
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteParent
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteParent.SprouteClass
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteParent.SproutePackage
import com.casadetasha.kexp.sproute.processor.models.objects.SprouteRequestAnnotations
import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedOptions(SprouteAnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
class SprouteAnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        lateinit var processingEnvironment: ProcessingEnvironment
    }

    private lateinit var roundEnv: RoundEnvironment
    private val sprouteClasses: Set<SprouteClass> by lazy { roundEnv.getRouteClasses() }
    private val sproutePackages: Set<SproutePackage> by lazy { roundEnv.getRoutePackages() }

    private val mergedKotlinParents: Set<SprouteParent> by lazy {
        sprouteClasses.toSortedSet() + sproutePackages.toSortedSet()
    }

    private lateinit var kaptKotlinGeneratedDir: String

    override fun getSupportedAnnotationTypes() = mutableSetOf(
        Sproute::class.java.canonicalName,
        SprouteRoot::class.java.canonicalName
    ) + SprouteRequestAnnotations.validRequestTypes.map { it.java.canonicalName }.toMutableList()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        AnnotationParser.setup(processingEnv)
        processingEnvironment = processingEnv
        kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return false

        if (roundEnv == null) return false
        this.roundEnv = roundEnv

        sprouteRoots.putAll(roundEnv.getSprouteRoots())
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
