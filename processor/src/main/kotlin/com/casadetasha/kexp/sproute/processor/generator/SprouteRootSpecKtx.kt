package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.MemberNames.MethodNames
import com.casadetasha.kexp.sproute.processor.SprouteNode
import com.casadetasha.kexp.sproute.processor.models.SprouteAuthentication
import com.squareup.kotlinpoet.FunSpec

internal fun FunSpec.Builder.amendToFunSpecBuilder(
    rootNode: SprouteNode,
    authentication: SprouteAuthentication
): FunSpec.Builder = apply {
    beginSetAuthenticationRequirements(authentication)
    addSprouteSpecs(rootNode)
    endSetAuthenticationRequirements(authentication)
}

private fun FunSpec.Builder.beginSetAuthenticationRequirements(authentication: SprouteAuthentication): FunSpec.Builder =
    apply {
        if (authentication.isAuthenticationRequested && authentication.hasAuthenticationParams) {
            beginControlFlow(
                "%M(%L) ",
                MethodNames.authenticationScopeMethod,
                authentication.authenticationParams
            )
        } else if (authentication.isAuthenticationRequested) {
            beginControlFlow("%M() ", MethodNames.authenticationScopeMethod)
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

internal fun FunSpec.Builder.amendSprouteSpec(node: SprouteNode, baseRouteSegment: String = "", fullParentRoute: String)
        : FunSpec.Builder = apply {
    val fullRoute = "$fullParentRoute/${node.name}"
    if (node.buds.isEmpty() && node.sproutes.size == 1) {
        return amendFunForSingleSprouteNode(baseRouteSegment, node, fullRoute)
    }

    amendFunForChildBearingNode(baseRouteSegment, node, fullRoute)
}

// route("/this/next/") `/this/next`@ {...
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
    beginNodeControlFlow(routeSegment = "${baseRouteSegment}/${node.name}", fullRoute = fullRoute)
    amendBudsFromNode(node, fullRoute)
    amendSproutesFromNode(node, fullRoute)
    endControlFlow()
}

internal fun FunSpec.Builder.amendSproutesFromNode(node: SprouteNode, fullRoute: String) = apply {
    node.sproutes.forEach {
        if (node.sproutes.first() != it || node.buds.isNotEmpty()) addStatement("")
        amendSprouteSpec(it, baseRouteSegment = "", fullParentRoute = fullRoute)
    }
}

internal fun FunSpec.Builder.amendBudsFromNode(node: SprouteNode, fullRoute: String): FunSpec.Builder = apply {
    node.buds.forEach {
        if (node.buds.first() != it) addStatement("")
        amendFunForBud(bud = it, fullRoutePath = fullRoute)
    }
}

// route("/routeSegment") {
private fun FunSpec.Builder.beginNodeControlFlow(routeSegment: String, fullRoute: String) {
    val routeReference: String = fullRoute.trim()
    if (routeReference.isEmpty()) {
        beginControlFlow(
            "%M(%S)",
            MethodNames.routeMethod,
            "/${routeSegment.removePrefix("/")}"
        )
    } else {
        beginControlFlow(
            "%M(%S)·%L@",
            MethodNames.routeMethod,
            "/${routeSegment.removePrefix("/")}",
            "`$routeReference`"
        )
    }

}

private fun FunSpec.Builder.endSetAuthenticationRequirements(authentication: SprouteAuthentication): FunSpec.Builder =
    apply {
        if (authentication.isAuthenticationRequested) endControlFlow()
    }