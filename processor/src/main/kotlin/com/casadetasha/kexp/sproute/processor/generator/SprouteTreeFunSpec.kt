package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.models.sproutes.tree.SprouteNode
import com.casadetasha.kexp.sproute.processor.models.sproutes.tree.SprouteTree
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteAuthentication
import com.casadetasha.kexp.sproute.processor.models.KotlinNames.GeneratedMethodNames
import com.casadetasha.kexp.sproute.processor.models.KotlinNames.MethodNames
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

    private fun FunSpec.Builder.beginSetAuthenticationRequirements(authentication: SprouteAuthentication)
            : FunSpec.Builder = apply {
        if (authentication.isAuthenticationRequested && authentication.hasAuthenticationParams) {
            beginParameterizedAuthFlow(authentication.authenticationParams)
        } else if (authentication.isAuthenticationRequested) {
            beginNonParameterizedAuthFlow()
        }
    }

    private fun FunSpec.Builder.addSprouteSpecs(rootNode: SprouteNode): FunSpec.Builder = apply {
        rootNode.sproutes.forEach {
            if (rootNode.sproutes.first() != it) addStatement("")
            amendSprouteSpec(
                node = it,
                fullParentRoute = ""
            )
        }
    }

    private fun FunSpec.Builder.endSetAuthenticationRequirements(authentication: SprouteAuthentication): FunSpec.Builder =
        apply {
            if (authentication.isAuthenticationRequested) endControlFlow()
        }
}
