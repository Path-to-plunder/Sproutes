package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.helpers.KotlinNames.GeneratedMethodNames
import com.casadetasha.kexp.sproute.processor.helpers.KotlinNames.MethodNames
import com.casadetasha.kexp.sproute.processor.models.SprouteNode
import com.casadetasha.kexp.sproute.processor.models.SprouteTree
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.squareup.kotlinpoet.FunSpec
import io.ktor.application.*
import java.util.*

internal class SprouteTreeFunSpec(private val sprouteTree: SprouteTree) {

    val value: FunSpec by lazy {
        FunSpec.builder(GeneratedMethodNames.SPROUTE_CONFIGURATION)
            .receiver(Application::class)
            .beginControlFlow("%M", MethodNames.routingMethod)
            .amendSproutesFromMap(sprouteTree.sprouteMap.toSortedMap())
            .endControlFlow()
            .build()
    }

    private fun FunSpec.Builder.amendSproutesFromMap(sprouteMap: SortedMap<SprouteAuthentication, SprouteNode>)
            : FunSpec.Builder = apply {
        sprouteMap.forEach {
            amendToFunSpecBuilder(
                authentication = it.key,
                rootNode = it.value
            )

            if (it.key != sprouteMap.lastKey()) addStatement("")
        }
    }

    private fun FunSpec.Builder.amendToFunSpecBuilder(
        rootNode: SprouteNode,
        authentication: SprouteAuthentication
    ): FunSpec.Builder = apply {
        beginSetAuthenticationRequirements(authentication)
        addSprouteSpecs(rootNode)
        endSetAuthenticationRequirements(authentication)
    }
}
