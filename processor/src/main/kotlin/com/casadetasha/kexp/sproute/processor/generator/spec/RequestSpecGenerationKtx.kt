package com.casadetasha.kexp.sproute.processor.generator.spec

import com.casadetasha.kexp.sproute.processor.generator.*
import com.casadetasha.kexp.sproute.processor.generator.tree.RequestFunctionNode
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteClass
import com.casadetasha.kexp.sproute.processor.sproutes.SproutePackage
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteParent
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteRequestFunction
import com.squareup.kotlinpoet.FunSpec
import io.ktor.application.*
import io.ktor.routing.*

internal fun FunSpec.Builder.amendFunForBud(requestRouteSegment: String = "", requestFunctionNode: RequestFunctionNode, fullRoutePath: String) {
    beginRequestControlFlow(requestRouteSegment, requestFunctionNode.function)                        //     get ("/path") {
    beginCallBlock(requestFunctionNode.function)                                                      //       call.respond(
    addMethodCall(requestFunctionNode.kotlinParent, requestFunctionNode.function, fullRoutePath)      //         Route().get()
    endCallBlock(requestFunctionNode.function)                                                        //       )
    endRequestControlFlow(requestRouteSegment)                                                        //     }
}

internal fun FunSpec.Builder.addMethodCall(
    sprouteKotlinParent: SprouteParent, function: SprouteRequestFunction, fullRoutePath: String
) = apply {
    when (sprouteKotlinParent) {
        is SprouteClass -> addRouteClassMethodCallCode(sprouteKotlinParent, function)
        is SproutePackage -> addRoutePackageMethodCall(function, fullRoutePath)
    }
}

private fun FunSpec.Builder.addRoutePackageMethodCall(function: SprouteRequestFunction, fullRoutePath: String) = apply {
    when (function.receiver) {
        Route::class.toMemberName() -> addRouteExtensionPackageMethodCallCode(function, fullRoutePath)
        ApplicationCall::class.toMemberName() -> addPackageMethodCallCode(function)
        null -> addPackageMethodCallCode(function)
    }
}

private fun FunSpec.Builder.addRouteExtensionPackageMethodCallCode(
    function: SprouteRequestFunction,
    fullRoutePath: String
) {
    val routeReference = fullRoutePath.trim()
    if (routeReference.isBlank()) {
        addRouteExtensionCodeWithoutRoute(function)
    } else {
        addRouteExtensionCodeWithRoute(function, routeReference)
    }
}

internal fun FunSpec.Builder.endRequestControlFlow(requestRouteSegment: String) = apply {
    if (requestRouteSegment.isBlank()) {
        addEndCurlyBrace()
    } else {
        endControlFlow()
    }
}
