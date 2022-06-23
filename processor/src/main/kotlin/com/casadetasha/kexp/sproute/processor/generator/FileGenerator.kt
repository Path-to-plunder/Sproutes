package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.annotationparser.AnnotationParser.printNote
import com.casadetasha.kexp.generationdsl.dsl.FileTemplate.Companion.generateFile
import com.casadetasha.kexp.sproute.processor.generator.template.generateConfigureSproutesMethod
import com.casadetasha.kexp.sproute.processor.generator.tree.SprouteTree

internal class FileGenerator(
    private val kaptKotlinGeneratedDir: String,
    private val sprouteTree: SprouteTree
) {

    companion object {
        const val SPROUTE_CONFIG_FILE_NAME = "Sproutes"
        const val ROUTING_PACKAGE_NAME = "com.casadetasha.kexp.sproute"
    }

    internal fun generateSproutes() {
        printNote("Generating sproute hierarchies")
        generatePublicRouteConfigFile()
    }

    private fun generatePublicRouteConfigFile() {
        generateFile(directory = kaptKotlinGeneratedDir,
            packageName = ROUTING_PACKAGE_NAME,
            fileName = SPROUTE_CONFIG_FILE_NAME) {
            generateConfigureSproutesMethod(sprouteTree)
        }.writeToDisk()
//        FileSpec.builder(ROUTING_PACKAGE_NAME, SPROUTE_CONFIG_FILE_NAME)
//            .addFunction(SprouteTreeFunSpec(sprouteTree).value).build()
//            .writeTo(File(kaptKotlinGeneratedDir))
    }
}
