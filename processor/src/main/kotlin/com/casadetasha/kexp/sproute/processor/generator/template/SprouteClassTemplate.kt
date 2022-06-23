package com.casadetasha.kexp.sproute.processor.generator.template

import com.casadetasha.kexp.generationdsl.dsl.CodeTemplate
import com.casadetasha.kexp.generationdsl.dsl.FileTemplate
import com.casadetasha.kexp.sproute.processor.generator.tree.SegmentNode
import com.casadetasha.kexp.sproute.processor.generator.tree.SprouteTree
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.Authentication
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.GeneratedMethodNames
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.MethodNames
import com.squareup.kotlinpoet.asTypeName
import io.ktor.server.application.*
import java.util.*

internal fun FileTemplate.generateConfigureSproutesMethod(sprouteTree: SprouteTree) {
    generateFunction(
        name = GeneratedMethodNames.SPROUTE_CONFIGURATION,
        receiverType = Application::class.asTypeName()
    ) {
        generateMethodBody {
            generateControlFlowCode("%M", MethodNames.routingMethod) {
                generateSproutesFromMap(sprouteTree.sprouteMap.toSortedMap())
            }
        }
    }
}

internal fun CodeTemplate.generateSproutesFromMap(sprouteMap: SortedMap<Authentication, SegmentNode>) {
    sprouteMap.forEach {
        generateSproute(
            authentication = it.key,
            rootNode = it.value
        )

        if (it.key != sprouteMap.lastKey()) generateNewLine()
    }
}

internal fun CodeTemplate.generateSproute(
    rootNode: SegmentNode,
    authentication: Authentication
) {
    if (authentication.isAuthenticationRequested) {
        generateAuthenticatedFlow(authentication) {
            generateSprouteSpecs(rootNode)
        }
    } else {
        generateSprouteSpecs(rootNode)
    }
}

internal fun CodeTemplate.generateSprouteSpecs(rootNode: SegmentNode) {
    rootNode.sproutes.forEach {
        if (rootNode.sproutes.first() != it) generateNewLine()
        generateSprouteSpec(
            node = it,
            fullParentRoute = ""
        )
    }
}

internal fun CodeTemplate.generateAuthenticatedFlow(authentication: Authentication, generateFlowBody: () -> Unit) {
    val authParams = authentication.authenticationParams
    if (authentication.hasAuthenticationParams) {
        generateControlFlowCode("%M(%L) ", MethodNames.authenticationScopeMethod, authParams) {
            generateFlowBody()
        }
    } else {
        generateControlFlowCode("%M ", MethodNames.authenticationScopeMethod) {
            generateFlowBody()
        }
    }
}
