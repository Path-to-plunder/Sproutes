package com.casadetasha.kexp.sproute.processor.generator.template

import com.casadetasha.kexp.generationdsl.dsl.CodeTemplate
import com.casadetasha.kexp.sproute.processor.generator.tree.SegmentNode
import com.casadetasha.kexp.sproute.processor.values.KotlinNames

internal fun CodeTemplate.generateSprouteSpec(
    node: SegmentNode,
    baseRouteSegment: String = "",
    fullParentRoute: String
) {
    val fullRoute = "$fullParentRoute/${node.name}"
    if (node.sortedRequestFunctionNodes.isEmpty() && node.sproutes.size == 1) {
        return amendFunForSingleSprouteNode(baseRouteSegment, node, fullRoute)
    }

    amendNodeWithChildren(baseRouteSegment, node, fullRoute)
}

private fun CodeTemplate.amendFunForSingleSprouteNode(baseRouteSegment: String, node: SegmentNode, fullRoute: String) {
    val aggregatedRouteSegment = "${baseRouteSegment}/${node.name}"
    node.sproutes.forEach {
        generateSprouteSpec(
            it,
            aggregatedRouteSegment,
            fullRoute
        )
    }
}

private fun CodeTemplate.amendNodeWithChildren(baseRouteSegment: String, node: SegmentNode, fullRoute: String) {
    val routeSegment = "${baseRouteSegment}/${node.name}"
    generateNodeControlFlow(routeSegment = routeSegment, fullRoute = fullRoute) {
        generateBudsFromNode(node, fullRoute)
        amendSproutesFromNode(node, fullRoute)
    }
}

internal fun CodeTemplate.amendSproutesFromNode(node: SegmentNode, fullRoute: String) {
    node.sproutes.forEach { segmentNode ->
        if (node.sproutes.first() != segmentNode || node.sortedRequestFunctionNodes.isNotEmpty()) {
            generateNewLine()
            generateNewLine()
        }
        generateSprouteSpec(segmentNode, baseRouteSegment = "", fullParentRoute = fullRoute)
    }
}

private fun CodeTemplate.generateBudsFromNode(node: SegmentNode, fullRoute: String) {
    node.sortedRequestFunctionNodes.forEach { requestNode ->
        generateFunForBud(requestFunctionNode = requestNode, fullRoutePath = fullRoute)
        if (node.sortedRequestFunctionNodes.last().function.simpleName != requestNode.function.simpleName) {
            generateNewLine()
            generateNewLine()
        }
    }
}

private fun CodeTemplate.generateNodeControlFlow(
    routeSegment: String,
    fullRoute: String,
    generateBody: CodeTemplate.() -> Unit
) {
    val routeReference: String = fullRoute.trim()
    if (routeReference.isEmpty()) {
        generateNodeControlFlowWithoutRouteRef(routeSegment) {
            generateBody()
        }
    } else {
        generateNodeControlFlowWithRouteRef(routeSegment, routeReference) {
            generateBody()
        }
    }
}

internal fun CodeTemplate.generateNodeControlFlowWithRouteRef(
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

internal fun CodeTemplate.generateNodeControlFlowWithoutRouteRef(
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
