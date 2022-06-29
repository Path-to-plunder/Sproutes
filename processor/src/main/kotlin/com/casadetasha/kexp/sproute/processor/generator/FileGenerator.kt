package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.annotationparser.AnnotationParser.printNote
import com.casadetasha.kexp.generationdsl.dsl.FileTemplate.Companion.generateFile
import com.casadetasha.kexp.sproute.processor.generator.template.generateConfigureSproutesFunction
import com.casadetasha.kexp.sproute.processor.generator.tree.SprouteTree

internal class FileGenerator(
    private val kaptKotlinGeneratedDir: String,
    private val sprouteTree: SprouteTree
) {

    internal fun generateSproutes() {
        printNote("Generating sproute hierarchies")
        generatePublicRouteConfigFile()
    }

    private fun generatePublicRouteConfigFile() {
        generateFile(directory = kaptKotlinGeneratedDir,
            packageName = ROUTING_PACKAGE_NAME,
            fileName = SPROUTE_CONFIG_FILE_NAME)
        {
            generateConfigureSproutesFunction(sprouteTree)
        }.writeToDisk()
    }

    companion object {
        const val SPROUTE_CONFIG_FILE_NAME = "Sproutes"
        const val ROUTING_PACKAGE_NAME = "com.casadetasha.kexp.sproute"
    }
}
