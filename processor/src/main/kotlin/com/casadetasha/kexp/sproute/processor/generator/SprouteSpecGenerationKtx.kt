package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.models.sproutes.tree.SprouteNode
import com.squareup.kotlinpoet.FunSpec

internal fun FunSpec.Builder.amendSprouteSpec(node: SprouteNode, baseRouteSegment: String = "", fullParentRoute: String)
        : FunSpec.Builder = apply {
    val fullRoute = "$fullParentRoute/${node.name}"
    if (node.sortedHttpRequestNodes.isEmpty() && node.sproutes.size == 1) {
        return amendFunForSingleSprouteNode(baseRouteSegment, node, fullRoute)
    }

    amendNodeWithChildren(baseRouteSegment, node, fullRoute)
}

private fun FunSpec.Builder.amendFunForSingleSprouteNode(baseRouteSegment: String, node: SprouteNode, fullRoute: String)
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

private fun FunSpec.Builder.amendNodeWithChildren(baseRouteSegment: String, node: SprouteNode, fullRoute: String)
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

private fun FunSpec.Builder.amendBudsFromNode(node: SprouteNode, fullRoute: String): FunSpec.Builder = apply {
    node.sortedHttpRequestNodes.forEach {
        if (node.sortedHttpRequestNodes.first() != it) addStatement("")
        amendFunForBud(httpRequestNode = it, fullRoutePath = fullRoute)
    }
}

internal fun FunSpec.Builder.amendSproutesFromNode(node: SprouteNode, fullRoute: String) = apply {
    node.sproutes.forEach {
        if (node.sproutes.first() != it || node.sortedHttpRequestNodes.isNotEmpty()) addStatement("")
        amendSprouteSpec(it, baseRouteSegment = "", fullParentRoute = fullRoute)
    }
}
