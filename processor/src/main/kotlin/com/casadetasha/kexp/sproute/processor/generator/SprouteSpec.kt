package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.MemberNames.MethodNames
import com.casadetasha.kexp.sproute.processor.SprouteNode
import com.casadetasha.kexp.sproute.processor.ktx.amendFunForBud
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import io.ktor.application.*

internal class SprouteSpec(private val rootNode: SprouteNode) {
    private lateinit var funBuilder: FunSpec.Builder

    val funSpec: FunSpec by lazy {
        funBuilder = FunSpec.builder("sprouteBuds").receiver(Application::class).addModifiers(KModifier.PRIVATE)
        funBuilder.beginControlFlow("%M", MethodNames.routingMethod)

        rootNode.sproutes.forEach {
            if (rootNode.sproutes.first() != it) funBuilder.addStatement("")
            amendFunForNode(it, fullParentRoute = "")
        }

        funBuilder.endControlFlow()
        funBuilder.build()
    }

    private fun amendFunForNode(node: SprouteNode, baseRouteSegment: String = "", fullParentRoute: String) {
        val fullRoute = "$fullParentRoute/${node.name}"
        if (node.buds.isEmpty() && node.sproutes.size == 1) {
            return amendFunForSingleSprouteNode(baseRouteSegment, node, fullRoute)
        }

        amendFunForChildBearingNode(baseRouteSegment, node, fullRoute)
    }

    private fun amendFunForSingleSprouteNode(baseRouteSegment: String, node: SprouteNode, fullRoute: String) {
        val aggregatedRouteSegment = "${baseRouteSegment}/${node.name}"
        node.sproutes.forEach { amendFunForNode(it, aggregatedRouteSegment, fullRoute ) } // route("/this/next/") `/this/next`@ {...
    }

    private fun amendFunForChildBearingNode(baseRouteSegment: String, node: SprouteNode, fullRoute: String) {
        beginNodeControlFlow(routeSegment = "${baseRouteSegment}/${node.name}", fullRoute = fullRoute)

        node.buds.forEach {
            if (node.buds.first() != it) funBuilder.addStatement("")
            funBuilder.amendFunForBud(bud = it, fullRoutePath = fullRoute)
        }

        node.sproutes.forEach {
            if (node.sproutes.first() != it || node.buds.isNotEmpty()) funBuilder.addStatement("")
            amendFunForNode(it, baseRouteSegment = "", fullParentRoute = fullRoute)
        }

        funBuilder.endControlFlow()
    }

    private fun beginNodeControlFlow(routeSegment: String, fullRoute: String) { // route("/routeSegment") {
        val routeReference: String = fullRoute.trim()
        if (routeReference.isEmpty()) {
            funBuilder.beginControlFlow(
                "%M(%S)",
                MethodNames.routeMethod,
                "/${routeSegment.removePrefix("/")}"
            )
        } else {
            funBuilder.beginControlFlow(
                "%M(%S)·%L@",
                MethodNames.routeMethod,
                "/${routeSegment.removePrefix("/")}",
                "`$routeReference`"
            )
        }
    }
}
