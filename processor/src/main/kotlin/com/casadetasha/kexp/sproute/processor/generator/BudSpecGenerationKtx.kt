package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.models.Bud
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteRequestFunction
import com.squareup.kotlinpoet.FunSpec
import io.ktor.application.*
import io.ktor.routing.*

internal fun FunSpec.Builder.amendFunForBud(requestRouteSegment: String = "", bud: Bud, fullRoutePath: String) {
    beginRequestControlFlow(requestRouteSegment, bud.function)          //     get ("/path") {
    beginCallBlock(bud.function)                                        //       call.respond(
    addMethodCall(bud.kotlinParent, bud.function, fullRoutePath)        //         Route().get()
    endCallBlock(bud.function)                                          //       )
    endRequestControlFlow(requestRouteSegment)                          //     }
}

internal fun FunSpec.Builder.addMethodCall(
    sprouteKotlinParent: SprouteKotlinParent, function: SprouteRequestFunction, fullRoutePath: String
) = apply {
    when (sprouteKotlinParent) {
        is SprouteKotlinParent.SprouteClass -> addRouteClassMethodCallCode(sprouteKotlinParent, function)
        is SprouteKotlinParent.SproutePackage -> addRoutePackageMethodCall(function, fullRoutePath)
    }
}

private fun FunSpec.Builder.addRoutePackageMethodCall(function: SprouteRequestFunction, fullRoutePath: String) = apply {
    when (function.receiver) {
        Route::class.toMemberName() -> addRouteExtensionPackageMethodCallCode(function, fullRoutePath)
        ApplicationCall::class.toMemberName() -> addPackageMethodCallCode(function)
        null -> addPackageMethodCallCode(function)
    }
}

private fun FunSpec.Builder.addRouteExtensionPackageMethodCallCode(function: SprouteRequestFunction, fullRoutePath: String) {
    val routeReference = fullRoutePath.trim()
    if (routeReference.isBlank()) {
        addRouteExtensionCodeWithoutRoute(function)
    } else  {
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
