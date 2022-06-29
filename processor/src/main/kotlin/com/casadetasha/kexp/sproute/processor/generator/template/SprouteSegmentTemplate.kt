package com.casadetasha.kexp.sproute.processor.generator.template

import com.casadetasha.kexp.generationdsl.dsl.CodeTemplate
import com.casadetasha.kexp.sproute.processor.generator.tree.SegmentNode
import com.casadetasha.kexp.sproute.processor.values.KotlinNames

internal fun CodeTemplate.generateSprouteSegment(
    node: SegmentNode,
    baseRouteSegment: String = "",
    fullParentRoute: String
) {
    val fullRoute = "$fullParentRoute/${node.name}"
    if (node.sortedRequestFunctionNodes.isEmpty() && node.sproutes.size == 1) {
        return mergeNextSegmentAndContinueGenerating(baseRouteSegment, node, fullRoute)
    }

    generateSprouteContent(baseRouteSegment, node, fullRoute)
}

private fun CodeTemplate.mergeNextSegmentAndContinueGenerating(baseRouteSegment: String, node: SegmentNode, fullRoute: String) {
    val aggregatedRouteSegment = "${baseRouteSegment}/${node.name}"
    node.sproutes.forEach {
        generateSprouteSegment(
            it,
            aggregatedRouteSegment,
            fullRoute
        )
    }
}

private fun CodeTemplate.generateSprouteContent(baseRouteSegment: String, node: SegmentNode, fullRoute: String) {
    val routeSegment = "${baseRouteSegment}/${node.name}"
    generateSprouteFlow(routeSegment = routeSegment, fullRoute = fullRoute) {
        generateRequests(node, fullRoute)
        generateSubSegments(node, fullRoute)
    }
}

internal fun CodeTemplate.generateSubSegments(node: SegmentNode, fullRoute: String) {
    node.sproutes.forEach { segmentNode ->
        if (node.sproutes.first() != segmentNode || node.sortedRequestFunctionNodes.isNotEmpty()) {
            generateNewLine(times = 2)
        }
        generateSprouteSegment(segmentNode, baseRouteSegment = "", fullParentRoute = fullRoute)
    }
}

private fun CodeTemplate.generateRequests(node: SegmentNode, fullRoute: String) {
    node.sortedRequestFunctionNodes.forEach { requestNode ->
        generateRequestBlock(requestFunctionNode = requestNode, fullRoutePath = fullRoute)
        if (node.sortedRequestFunctionNodes.last().function.simpleName != requestNode.function.simpleName) {
            generateNewLine(times = 2)
        }
    }
}

private fun CodeTemplate.generateSprouteFlow(
    routeSegment: String,
    fullRoute: String,
    generateBody: CodeTemplate.() -> Unit
) {
    val routeReference: String = fullRoute.trim()
    if (routeReference.isEmpty()) {
        generateSprouteFlowWithoutPath(routeSegment) {
            generateBody()
        }
    } else {
        generateSprouteFlowWithPath(routeSegment, routeReference) {
            generateBody()
        }
    }
}

internal fun CodeTemplate.generateSprouteFlowWithPath(
    routeSegment: String, routeReference: String,
    generateBody: CodeTemplate.() -> Unit
) {
    generateControlFlowCode(
        "%M(%S)·%L@",
        KotlinNames.MethodNames.routeMethod,
        "/${routeSegment.removePrefix("/")}",
        "`$routeReference`",
        endFlowString = "\n}"
    ) { generateBody() }
}

internal fun CodeTemplate.generateSprouteFlowWithoutPath(
    routeSegment: String,
    generateBody: CodeTemplate.() -> Unit
) {
    generateControlFlowCode(
        "%M(%S)",
        KotlinNames.MethodNames.routeMethod,
        "/${routeSegment.removePrefix("/")}",
        endFlowString = "\n}"
    ) { generateBody() }
}
