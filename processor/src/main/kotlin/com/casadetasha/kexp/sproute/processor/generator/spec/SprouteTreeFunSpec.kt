package com.casadetasha.kexp.sproute.processor.generator.spec

import com.casadetasha.kexp.sproute.processor.generator.beginNonParameterizedAuthFlow
import com.casadetasha.kexp.sproute.processor.generator.beginParameterizedAuthFlow
import com.casadetasha.kexp.sproute.processor.generator.tree.SegmentNode
import com.casadetasha.kexp.sproute.processor.generator.tree.SprouteTree
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.Authentication
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.GeneratedMethodNames
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.MethodNames
import com.squareup.kotlinpoet.FunSpec
import io.ktor.server.application.*
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

    private fun FunSpec.Builder.amendSproutesFromMap(sprouteMap: SortedMap<Authentication, SegmentNode>)
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
        rootNode: SegmentNode,
        authentication: Authentication
    ): FunSpec.Builder = apply {
        beginSetAuthenticationRequirements(authentication)
        addSprouteSpecs(rootNode)
        endSetAuthenticationRequirements(authentication)
    }

    private fun FunSpec.Builder.beginSetAuthenticationRequirements(authentication: Authentication)
            : FunSpec.Builder = apply {
        if (authentication.isAuthenticationRequested && authentication.hasAuthenticationParams) {
            beginParameterizedAuthFlow(authentication.authenticationParams)
        } else if (authentication.isAuthenticationRequested) {
            beginNonParameterizedAuthFlow()
        }
    }

    private fun FunSpec.Builder.addSprouteSpecs(rootNode: SegmentNode): FunSpec.Builder = apply {
        rootNode.sproutes.forEach {
            if (rootNode.sproutes.first() != it) addStatement("")
            amendSprouteSpec(
                node = it,
                fullParentRoute = ""
            )
        }
    }

    private fun FunSpec.Builder.endSetAuthenticationRequirements(authentication: Authentication): FunSpec.Builder =
        apply {
            if (authentication.isAuthenticationRequested) endControlFlow()
        }
}
