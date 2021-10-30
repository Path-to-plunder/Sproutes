package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.Bud
import com.casadetasha.kexp.sproute.processor.MemberNames.MethodNames
import com.casadetasha.kexp.sproute.processor.SprouteNode
import com.casadetasha.kexp.sproute.processor.ktx.addMethodParameters
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestFunction
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import io.ktor.application.*
import io.ktor.routing.*

internal class SprouteTrieSpec(private val rootNode: SprouteNode) {
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
        beginNodeControlFlow(routeSegment = "${baseRouteSegment}/${node.name}", fullRoute = fullRoute)        // route("/routeSegment") {
        node.buds.forEach {
            if (node.buds.first() != it) funBuilder.addStatement("")
            amendFunForBud(bud = it, fullRoutePath = fullRoute)
        }
        node.sproutes.forEach {
            if (node.sproutes.first() != it || node.buds.isNotEmpty()) funBuilder.addStatement("")
            amendFunForNode(it, baseRouteSegment = "", fullParentRoute = fullRoute)
        }
        funBuilder.endControlFlow()                                                                           // }
    }

    private fun beginNodeControlFlow(routeSegment: String, fullRoute: String) {
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

    private fun amendFunForBud(requestRouteSegment: String = "", bud: Bud, fullRoutePath: String) {
        beginRequestControlFlow(requestRouteSegment, bud.function)          //     get ("/path") {
        beginCallBlock(bud.function)                                        //       call.respond(
        addMethodCall(bud.kotlinParent, bud.function, fullRoutePath)        //         Route().get()
        endCallBlock(bud.function)                                          //       )
        endRequestControlFlow(requestRouteSegment)                          //     }
    }

    private fun beginRequestControlFlow(path: String, function: SprouteRequestFunction) = apply {
        if (path.isBlank()) {
            funBuilder.addCode(
                "%M·{·",
                function.requestMethodName
            )
        } else {
            funBuilder.beginControlFlow(
                "%M(%S)·",
                function.requestMethodName,
                path,
            )
        }
    }

    private fun beginCallBlock(function: SprouteRequestFunction) = apply {
        if (function.hasReturnValue) {
            funBuilder.addCode(
                "%M.%M(·",
                MethodNames.applicationCallGetter,
                MethodNames.callRespondMethod,
            )
        }
        if (function.isApplicationCallExtensionMethod) {
            funBuilder.addCode(
                "·%M.%M·{·",
                MethodNames.applicationCallGetter,
                MethodNames.applyMethod
            )
        }
    }

    private fun addRoutePackageMethodCall(function: SprouteRequestFunction, fullRoutePath: String) = apply {
        when (function.receiver) {
            Route::class.toMemberName() -> addRouteExtensionPackageMethodCall(function, fullRoutePath)
            ApplicationCall::class.toMemberName() -> addPackageMethodCall(function)
            null -> addPackageMethodCall(function)
        }
    }

    private fun addRouteExtensionPackageMethodCall(function: SprouteRequestFunction, fullRoutePath: String) {
        val routeReference = fullRoutePath.trim()
        if (routeReference.isBlank()) {
            funBuilder.addCode(
                CodeBlock.builder()
                    .add(
                        "%L%N.%M",
                        "this@",
                        MethodNames.routeMethod,
                        function.memberName
                    )
                    .addMethodParameters(function.params)
                    .build()
            )
        } else  {
            funBuilder.addCode(
                CodeBlock.builder()
                    .add(
                        "%L%L.%M",
                        "this@",
                        "`$routeReference`",
                        function.memberName
                    )
                    .addMethodParameters(function.params)
                    .build()
            )
        }
    }

    private fun addMethodCall(
        sprouteKotlinParent: SprouteKotlinParent, function: SprouteRequestFunction, fullRoutePath: String
    ) = apply {
        when (sprouteKotlinParent) {
            is SprouteKotlinParent.SprouteClass -> addRouteClassMethodCall(sprouteKotlinParent, function)
            is SprouteKotlinParent.SproutePackage -> addRoutePackageMethodCall(function, fullRoutePath)
        }
    }

    private fun addRouteClassMethodCall(
        sprouteKotlinParent: SprouteKotlinParent, function: SprouteRequestFunction
    ) = apply {
        funBuilder.addCode(
            CodeBlock.builder()
                .add("%M", sprouteKotlinParent.memberName)
                .addMethodParameters((sprouteKotlinParent as SprouteKotlinParent.SprouteClass).primaryConstructorParams)
                .add(".%N", function.simpleName)
                .addMethodParameters(function.params)
                .build()
        )
    }

    private fun addPackageMethodCall(function: SprouteRequestFunction) {
        funBuilder.addCode(
            CodeBlock.builder()
                .add("%M", function.memberName)
                .addMethodParameters(function.params)
                .build()
        )
    }

    private fun endCallBlock(function: SprouteRequestFunction) = apply {
        if (function.isApplicationCallExtensionMethod) {
            funBuilder.addCode("·}")
        }
        if (function.hasReturnValue) {
            funBuilder.addCode("·)")
        } else {
            funBuilder.addCode("")
        }
    }

    private fun endRequestControlFlow(requestRouteSegment: String) = apply {
        if (requestRouteSegment.isBlank()) {
            funBuilder.addStatement("·}")
        } else {
            funBuilder.endControlFlow()
        }
    }
}
