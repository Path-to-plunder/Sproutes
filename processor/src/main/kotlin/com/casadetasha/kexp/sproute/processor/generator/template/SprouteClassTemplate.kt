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

internal fun FileTemplate.generateConfigureSproutesFunction(sprouteTree: SprouteTree) {
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
        generateSproutesForSegment(
            authentication = it.key,
            segment = it.value
        )

        if (it.key != sprouteMap.lastKey()) generateNewLine()
    }
}

internal fun CodeTemplate.generateSproutesForSegment(
    segment: SegmentNode,
    authentication: Authentication
) {
    if (authentication.isAuthenticationRequested) {
        generateAuthenticatedFlow(authentication) {
            generateSproutesForSegment(segment)
        }
    } else {
        generateSproutesForSegment(segment)
    }
}

internal fun CodeTemplate.generateSproutesForSegment(segment: SegmentNode) {
    segment.sproutes.forEach {
        if (segment.sproutes.first() != it) {
            generateNewLine(times = 2)
        }
        generateSprouteSegment(
            node = it,
            fullParentRoute = ""
        )
    }
}

internal fun CodeTemplate.generateAuthenticatedFlow(authentication: Authentication, generateFlowBody: () -> Unit) {
    val authParams = authentication.authenticationParams
    if (authentication.hasAuthenticationParams) {
        generateControlFlowCode("\n%M(%L) ", MethodNames.authenticationScopeMethod, authParams, endFlowString = "\n}") {
            generateFlowBody()
        }
    } else {
        generateControlFlowCode("\n%M ", MethodNames.authenticationScopeMethod, endFlowString = "\n}") {
            generateFlowBody()
        }
    }
}
