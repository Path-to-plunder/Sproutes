package com.casadetasha.kexp.sproute.processor.generator.spec

import com.casadetasha.kexp.sproute.processor.generator.beginNodeControlFlowWithRouteRef
import com.casadetasha.kexp.sproute.processor.generator.beginNodeControlFlowWithoutRouteRef
import com.casadetasha.kexp.sproute.processor.generator.tree.SegmentNode
import com.squareup.kotlinpoet.FunSpec

internal fun FunSpec.Builder.amendSprouteSpec(node: SegmentNode, baseRouteSegment: String = "", fullParentRoute: String)
        : FunSpec.Builder = apply {
    val fullRoute = "$fullParentRoute/${node.name}"
    if (node.sortedRequestFunctionNodes.isEmpty() && node.sproutes.size == 1) {
        return amendFunForSingleSprouteNode(baseRouteSegment, node, fullRoute)
    }

    amendNodeWithChildren(baseRouteSegment, node, fullRoute)
}

private fun FunSpec.Builder.amendFunForSingleSprouteNode(baseRouteSegment: String, node: SegmentNode, fullRoute: String)
        : FunSpec.Builder = apply {
    val aggregatedRouteSegment = "${baseRouteSegment}/${node.name}"
    node.sproutes.forEach {
        amendSprouteSpec(
            it,
            aggregatedRouteSegment,
            fullRoute
        )
    }
}

private fun FunSpec.Builder.amendNodeWithChildren(baseRouteSegment: String, node: SegmentNode, fullRoute: String)
        : FunSpec.Builder = apply {
    val routeSegment = "${baseRouteSegment}/${node.name}"
    beginNodeControlFlow(routeSegment = routeSegment, fullRoute = fullRoute)    // route("/this/next/") `/this/next`@ {
    amendBudsFromNode(node, fullRoute)                                          // ...
    amendSproutesFromNode(node, fullRoute)                                      // }
    endControlFlow()
}

private fun FunSpec.Builder.beginNodeControlFlow(routeSegment: String, fullRoute: String)
        : FunSpec.Builder = apply {
    val routeReference: String = fullRoute.trim()
    if (routeReference.isEmpty()) {
        beginNodeControlFlowWithoutRouteRef(routeSegment)
    } else {
        beginNodeControlFlowWithRouteRef(routeSegment, routeReference)
    }
}

private fun FunSpec.Builder.amendBudsFromNode(node: SegmentNode, fullRoute: String): FunSpec.Builder = apply {
    node.sortedRequestFunctionNodes.forEach {
        if (node.sortedRequestFunctionNodes.first() != it) addStatement("")
        amendFunForBud(requestFunctionNode = it, fullRoutePath = fullRoute)
    }
}

internal fun FunSpec.Builder.amendSproutesFromNode(node: SegmentNode, fullRoute: String) = apply {
    node.sproutes.forEach {
        if (node.sproutes.first() != it || node.sortedRequestFunctionNodes.isNotEmpty()) addStatement("")
        amendSprouteSpec(it, baseRouteSegment = "", fullParentRoute = fullRoute)
    }
}
