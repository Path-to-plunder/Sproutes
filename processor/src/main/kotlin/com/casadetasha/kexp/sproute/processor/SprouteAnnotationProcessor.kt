package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.annotationparser.AnnotationParser
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.generator.FileGenerator
import com.casadetasha.kexp.sproute.processor.generator.TrieFileGenerator
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

    private val sortedSprouteKotlinParents: Set<SprouteKotlinParent> by lazy {
        val mergedSortedSets = sprouteClasses.toSortedSet() + sproutePackages.toSortedSet()
        mergedSortedSets.toSet()
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
        SprouteRoots.putAll(roundEnv.getSprouteRoots())

        this.roundEnv = roundEnv
        // TODO: Investigate why process started running again without any @Route classes
        if (sortedSprouteKotlinParents.isNotEmpty()) {
            val routeTrie = generateRouteTrie(sortedSprouteKotlinParents)
            FileGenerator(kaptKotlinGeneratedDir).generateRouteFiles(sortedSprouteKotlinParents)
            val packageName = sortedSprouteKotlinParents.first().packageName
            TrieFileGenerator(kaptKotlinGeneratedDir = kaptKotlinGeneratedDir,
                rootNode = routeTrie,
                packageName
            ).generateRoutes()
        }
        return true
    }

}
