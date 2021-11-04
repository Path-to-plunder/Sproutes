package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.Bud
import com.casadetasha.kexp.sproute.processor.MemberNames
import com.casadetasha.kexp.sproute.processor.ktx.ifNotEmpty
import com.casadetasha.kexp.sproute.processor.ktx.orElse
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestFunction
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import io.ktor.application.*
import io.ktor.routing.*

internal fun FunSpec.Builder.amendFunForBud(requestRouteSegment: String = "", bud: Bud, fullRoutePath: String) {
    beginRequestControlFlow(requestRouteSegment, bud.function)          //     get ("/path") {
    beginCallBlock(bud.function)                                        //       call.respond(
    addMethodCall(bud.kotlinParent, bud.function, fullRoutePath)        //         Route().get()
    endCallBlock(bud.function)                                          //       )
    endRequestControlFlow(requestRouteSegment)                          //     }
}

private fun FunSpec.Builder.beginRequestControlFlow(path: String, function: SprouteRequestFunction) = apply {
    if (path.isBlank()) {
        addCode(
            "%M·{·",
            function.requestMethodName
        )
    } else {
        beginControlFlow(
            "%M(%S)·",
            function.requestMethodName,
            path,
        )
    }
}

private fun FunSpec.Builder.beginCallBlock(function: SprouteRequestFunction) = apply {
    if (function.hasReturnValue) {
        addCode(
            "%M.%M(·",
            MemberNames.MethodNames.applicationCallGetter,
            MemberNames.MethodNames.callRespondMethod,
        )
    }
    if (function.isApplicationCallExtensionMethod) {
        addCode(
            "·%M.%M·{·",
            MemberNames.MethodNames.applicationCallGetter,
            MemberNames.MethodNames.applyMethod
        )
    }
}

private fun FunSpec.Builder.addRoutePackageMethodCall(function: SprouteRequestFunction, fullRoutePath: String) = apply {
    when (function.receiver) {
        Route::class.toMemberName() -> addRouteExtensionPackageMethodCall(function, fullRoutePath)
        ApplicationCall::class.toMemberName() -> addPackageMethodCall(function)
        null -> addPackageMethodCall(function)
    }
}

private fun FunSpec.Builder.addRouteExtensionPackageMethodCall(function: SprouteRequestFunction, fullRoutePath: String) {
    val routeReference = fullRoutePath.trim()
    if (routeReference.isBlank()) {
        addCode(
            CodeBlock.builder()
                .add(
                    "%L%N.%M",
                    "this@",
                    MemberNames.MethodNames.routeMethod,
                    function.memberName
                )
                .addMethodParameters(function.params)
                .build()
        )
    } else  {
        addCode(
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

private fun FunSpec.Builder.addMethodCall(
    sprouteKotlinParent: SprouteKotlinParent, function: SprouteRequestFunction, fullRoutePath: String
) = apply {
    when (sprouteKotlinParent) {
        is SprouteKotlinParent.SprouteClass -> addRouteClassMethodCall(sprouteKotlinParent, function)
        is SprouteKotlinParent.SproutePackage -> addRoutePackageMethodCall(function, fullRoutePath)
    }
}

private fun FunSpec.Builder.addRouteClassMethodCall(
    sprouteKotlinParent: SprouteKotlinParent, function: SprouteRequestFunction
) = apply {
    addCode(
        CodeBlock.builder()
            .add("%M", sprouteKotlinParent.memberName)
            .addMethodParameters((sprouteKotlinParent as SprouteKotlinParent.SprouteClass).primaryConstructorParams)
            .add(".%N", function.simpleName)
            .addMethodParameters(function.params)
            .build()
    )
}

private fun FunSpec.Builder.addPackageMethodCall(function: SprouteRequestFunction) {
    addCode(
        CodeBlock.builder()
            .add("%M", function.memberName)
            .addMethodParameters(function.params)
            .build()
    )
}

private fun FunSpec.Builder.endCallBlock(function: SprouteRequestFunction) = apply {
    if (function.isApplicationCallExtensionMethod) {
        addCode("·}")
    }
    if (function.hasReturnValue) {
        addCode("·)")
    } else {
        addCode("")
    }
}

private fun FunSpec.Builder.endRequestControlFlow(requestRouteSegment: String) = apply {
    if (requestRouteSegment.isBlank()) {
        addStatement("·}")
    } else {
        endControlFlow()
    }
}

private fun CodeBlock.Builder.addMethodParameters(methodParams: List<MemberName>?) = apply {
    methodParams.ifNotEmpty {
        val memberParamString = it.joinToString(", ") { "%M" }
        val parameters = "($memberParamString)"
        add(parameters, *it.toTypedArray())
    }.orElse {
        add("()")
    }
}
