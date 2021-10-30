package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.SprouteNode
import com.casadetasha.kexp.sproute.processor.ktx.printNote
import com.squareup.kotlinpoet.FileSpec
import java.io.File

internal class SprouteFileGenerator(
    private val kaptKotlinGeneratedDir: String,
    private val rootNode: SprouteNode
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
            .addFunction(RouteConfigurationSpecParser().routeConfigurationFunSpec)
            .addFunction(SprouteSpec(rootNode).funSpec)
            .build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }

}
