package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.ktx.ifNotEmpty
import com.casadetasha.kexp.sproute.processor.ktx.orElse
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteRequestFunction
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import io.ktor.application.*
import io.ktor.routing.*


internal fun FunSpec.Builder.beginSetAuthenticationRequirements(authentication: SprouteAuthentication)
        : FunSpec.Builder = apply {
    if (authentication.isAuthenticationRequested && authentication.hasAuthenticationParams) {
        beginParameterizedAuthFlow(authentication.authenticationParams)
    } else if (authentication.isAuthenticationRequested) {
        beginNonParameterizedAuthFlow()
    }
}

internal fun FunSpec.Builder.beginNodeControlFlow(routeSegment: String, fullRoute: String)
        : FunSpec.Builder = apply {
    val routeReference: String = fullRoute.trim()
    if (routeReference.isEmpty()) {
        beginNodeControlFlowWithoutRouteRef(routeSegment)
    } else {
        beginNodeControlFlowWithRouteRef(routeSegment, routeReference)
    }
}

internal fun CodeBlock.Builder.addMethodParameters(methodParams: List<MemberName>?) = apply {
    methodParams.ifNotEmpty {
        addMethodParams(it)
    }.orElse {
        addEmptyMethodParamBrackets()
    }
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

internal fun FunSpec.Builder.endSetAuthenticationRequirements(authentication: SprouteAuthentication): FunSpec.Builder =
    apply {
        if (authentication.isAuthenticationRequested) endControlFlow()
    }
