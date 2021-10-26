package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.SprouteNode
import com.casadetasha.kexp.sproute.processor.ktx.printNote
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.squareup.kotlinpoet.FileSpec
import java.io.File

internal class TrieFileGenerator(private val kaptKotlinGeneratedDir: String,
                                 private val rootNode: SprouteNode,
                                 private val packageName: String) {

    companion object {
        const val INSTA_ROUTE_CONFIG_FILE_NAME = "Sproutes"
        const val SPROUTE_FILE_NAME = "BuddedSproutes"
        const val ROUTING_PACKAGE_NAME = "com.casadetasha.kexp.sproute"
    }

    internal fun generateRoutes() {
        processingEnvironment.printNote("Generating sproute hierarchies")
        generateRoutesForNode(rootNode)
    }

    private fun generateRoutesForNode(rootNode: SprouteNode) {
        SprouteTrieSpec(rootNode).funSpec
        FileSpec.builder(ROUTING_PACKAGE_NAME, SPROUTE_FILE_NAME)
            .addFunction(SprouteTrieSpec(rootNode).funSpec)
            .build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }

}
