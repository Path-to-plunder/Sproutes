package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.models.Bud
import com.casadetasha.kexp.sproute.processor.models.SprouteNode
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.squareup.kotlinpoet.FunSpec

internal fun FunSpec.Builder.addSprouteSpecs(rootNode: SprouteNode): FunSpec.Builder = apply {
    rootNode.sproutes.forEach {
        if (rootNode.sproutes.first() != it) addStatement("")
        amendSprouteSpec(
            node = it,
            fullParentRoute = ""
        )
    }
}

private fun FunSpec.Builder.amendSprouteSpec(node: SprouteNode, baseRouteSegment: String = "", fullParentRoute: String)
        : FunSpec.Builder = apply {
    val fullRoute = "$fullParentRoute/${node.name}"
    if (node.buds.isEmpty() && node.sproutes.size == 1) {
        return amendFunForSingleSprouteNode(baseRouteSegment, node, fullRoute)
    }

    amendFunForChildBearingNode(baseRouteSegment, node, fullRoute)
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

private fun FunSpec.Builder.amendFunForChildBearingNode(baseRouteSegment: String, node: SprouteNode, fullRoute: String)
= apply {
    val routeSegment ="${baseRouteSegment}/${node.name}"
    beginNodeControlFlow(routeSegment = routeSegment, fullRoute = fullRoute)    // route("/this/next/") `/this/next`@ {
    amendBudsFromNode(node, fullRoute)                                          // ...
    amendSproutesFromNode(node, fullRoute)                                      // }
    endControlFlow()
}

internal fun FunSpec.Builder.amendSproutesFromNode(node: SprouteNode, fullRoute: String) = apply {
    node.sproutes.forEach {
        if (node.sproutes.first() != it || node.buds.isNotEmpty()) addStatement("")
        amendSprouteSpec(it, baseRouteSegment = "", fullParentRoute = fullRoute)
    }
}

private fun FunSpec.Builder.amendBudsFromNode(node: SprouteNode, fullRoute: String): FunSpec.Builder = apply {
    node.buds.forEach {
        if (node.buds.first() != it) addStatement("")
        amendFunForBud(bud = it, fullRoutePath = fullRoute)
    }
}

private fun FunSpec.Builder.amendFunForBud(requestRouteSegment: String = "", bud: Bud, fullRoutePath: String) {
    beginRequestControlFlow(requestRouteSegment, bud.function)          //     get ("/path") {
    beginCallBlock(bud.function)                                        //       call.respond(
    addMethodCall(bud.kotlinParent, bud.function, fullRoutePath)        //         Route().get()
    endCallBlock(bud.function)                                          //       )
    endRequestControlFlow(requestRouteSegment)                          //     }
}
