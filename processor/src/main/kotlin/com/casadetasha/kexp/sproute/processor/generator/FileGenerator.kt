package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.generator.spec.SprouteTreeFunSpec
import com.casadetasha.kexp.sproute.processor.generator.tree.SprouteTree
import com.casadetasha.kexp.sproute.processor.ktx.printNote
import com.squareup.kotlinpoet.FileSpec
import java.io.File

internal class FileGenerator(
    private val kaptKotlinGeneratedDir: String,
    private val sprouteTree: SprouteTree
) {

    companion object {
        const val INSTA_ROUTE_CONFIG_FILE_NAME = "Sproutes"
        const val ROUTING_PACKAGE_NAME = "com.casadetasha.kexp.sproute"
    }

    internal fun generateSproutes() {
        processingEnvironment.printNote("Generating sproute hierarchies")
        generatePublicRouteConfigFile()
    }

    private fun generatePublicRouteConfigFile() {
        FileSpec.builder(ROUTING_PACKAGE_NAME, INSTA_ROUTE_CONFIG_FILE_NAME)
            .addFunction(SprouteTreeFunSpec(sprouteTree).value).build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }
}
