package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.ktx.ifNotEmpty
import com.casadetasha.kexp.sproute.processor.ktx.orElse
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteParent
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteRequestFunction
import com.casadetasha.kexp.sproute.processor.models.KotlinNames
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteClass
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName

internal fun FunSpec.Builder.beginParameterizedAuthFlow(authenticationParams: String)
        : FunSpec.Builder = apply {
    beginControlFlow(
        "%M(%L) ",
        KotlinNames.MethodNames.authenticationScopeMethod,
        authenticationParams
    )
}

internal fun FunSpec.Builder.beginNonParameterizedAuthFlow()
        : FunSpec.Builder = apply {
    beginControlFlow("%M() ", KotlinNames.MethodNames.authenticationScopeMethod)
}

internal fun FunSpec.Builder.beginRequestControlFlow(path: String, function: SprouteRequestFunction) = apply {
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

internal fun FunSpec.Builder.beginCallBlock(function: SprouteRequestFunction) = apply {
    if (function.hasReturnValue) {
        addCode(
            "%M.%M(·",
            KotlinNames.MethodNames.applicationCallGetter,
            KotlinNames.MethodNames.callRespondMethod,
        )
    }
    if (function.isApplicationCallExtensionMethod) {
        addCode(
            "·%M.%M·{·",
            KotlinNames.MethodNames.applicationCallGetter,
            KotlinNames.MethodNames.applyMethod
        )
    }
}

internal fun FunSpec.Builder.beginNodeControlFlowWithRouteRef(routeSegment: String, routeReference: String)
        : FunSpec.Builder = apply {
    beginControlFlow(
        "%M(%S)·%L@",
        KotlinNames.MethodNames.routeMethod,
        "/${routeSegment.removePrefix("/")}",
        "`$routeReference`"
    )
}

internal fun FunSpec.Builder.beginNodeControlFlowWithoutRouteRef(routeSegment: String) = apply {
    beginControlFlow(
        "%M(%S)",
        KotlinNames.MethodNames.routeMethod,
        "/${routeSegment.removePrefix("/")}"
    )
}

internal fun FunSpec.Builder.addRouteClassMethodCallCode(
    sprouteKotlinParent: SprouteParent, function: SprouteRequestFunction
) = apply {
    addCode(
        CodeBlock.builder()
            .add("%M", sprouteKotlinParent.memberName)
            .addMethodParameters((sprouteKotlinParent as SprouteClass).primaryConstructorParams)
            .add(".%N", function.simpleName)
            .addMethodParameters(function.params)
            .build()
    )
}

internal fun FunSpec.Builder.addRouteExtensionCodeWithoutRoute(function: SprouteRequestFunction): FunSpec.Builder =
    apply {
        addCode(
            CodeBlock.builder()
                .add(
                    "%L%N.%M",
                    "this@",
                    KotlinNames.MethodNames.routeMethod,
                    function.memberName
                )
                .addMethodParameters(function.params)
                .build()
        )
    }

internal fun FunSpec.Builder.addRouteExtensionCodeWithRoute(function: SprouteRequestFunction, routeReference: String) =
    apply {
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

internal fun FunSpec.Builder.addPackageMethodCallCode(function: SprouteRequestFunction) {
    addCode(
        CodeBlock.builder()
            .add("%M", function.memberName)
            .addMethodParameters(function.params)
            .build()
    )
}

internal fun FunSpec.Builder.endCallBlock(function: SprouteRequestFunction) = apply {
    if (function.isApplicationCallExtensionMethod) {
        addCode("·}")
    }
    if (function.hasReturnValue) {
        addCode("·)")
    } else {
        addCode("")
    }
}

internal fun FunSpec.Builder.addEndCurlyBrace() = apply { addStatement("·}") }

private fun CodeBlock.Builder.addMethodParameters(methodParams: List<MemberName>?) = apply {
    methodParams.ifNotEmpty {
        addMethodParams(it)
    }.orElse {
        addEmptyMethodParamBrackets()
    }
}

private fun CodeBlock.Builder.addEmptyMethodParamBrackets() = apply { add("()") }

private fun CodeBlock.Builder.addMethodParams(memberNames: List<MemberName>) = apply {
    val memberParamString = memberNames.joinToString(", ") { "%M" }
    val parameters = "($memberParamString)"
    add(parameters, *memberNames.toTypedArray())
}
