package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.ktx.printNote
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

internal class FileGenerator(private val processingEnv: ProcessingEnvironment,
                             private val kaptKotlinGeneratedDir: String) {

    companion object {
        const val INSTA_ROUTE_CONFIG_FILE_NAME = "Sproutes"
        const val ROUTING_PACKAGE_NAME = "com.casadetasha.kexp.sproute"
    }

    internal fun generateRouteFiles(routeClasses: ImmutableSet<SprouteKotlinParent>) {
        processingEnv.printNote("Creating route for $INSTA_ROUTE_CONFIG_FILE_NAME")
        routeClasses.forEach { generateRouteFile(it) }
        generatePublicRouteConfigFile(routeClasses)
    }

    private fun generateRouteFile(sprouteKotlinParent: SprouteKotlinParent) {
        processingEnv.printNote("Creating routes for ${sprouteKotlinParent.memberName}")

        FileSpec.builder(sprouteKotlinParent.packageName, sprouteKotlinParent.fileName)
            .addFunction(RouteFileSpecParser(sprouteKotlinParent).configurationFunSpec)
            .addRequestFunctions(sprouteKotlinParent)
            .build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun generatePublicRouteConfigFile(sprouteKotlinParents: ImmutableSet<SprouteKotlinParent>) {
        FileSpec.builder(ROUTING_PACKAGE_NAME, INSTA_ROUTE_CONFIG_FILE_NAME)
            .addFunction(RouteConfigurationSpecParser(sprouteKotlinParents).routeConfigurationFunSpec)
            .build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun FileSpec.Builder.addRequestFunctions(routeClass: SprouteKotlinParent) = apply {
        routeClass.requestFunction.forEach { requestFunction ->
            addFunction(RequestFunSpecParser(routeClass, requestFunction).funSpec)
        }
    }
}
