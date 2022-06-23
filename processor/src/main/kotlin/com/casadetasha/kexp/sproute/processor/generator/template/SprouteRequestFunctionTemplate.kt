package com.casadetasha.kexp.sproute.processor.generator.template

import com.casadetasha.kexp.generationdsl.dsl.CodeTemplate
import com.casadetasha.kexp.sproute.processor.generator.tree.RequestFunctionNode
import com.casadetasha.kexp.sproute.processor.ktx.ifNotEmpty
import com.casadetasha.kexp.sproute.processor.ktx.orElse
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteClass
import com.casadetasha.kexp.sproute.processor.sproutes.SproutePackage
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteParent
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteRequestFunction
import com.casadetasha.kexp.sproute.processor.values.KotlinNames
import com.squareup.kotlinpoet.MemberName
import io.ktor.server.application.*
import io.ktor.server.routing.*

internal fun CodeTemplate.generateFunForBud(
    requestFunctionNode: RequestFunctionNode,
    fullRoutePath: String
) {
    val function = requestFunctionNode.function

    generateControlFlowCode("%M", function.requestMethodName, beginFlowString = "·{·", endFlowString ="" ) {
        generateCallBlock(function) {
            generateMethodCall(requestFunctionNode.kotlinParent, function, fullRoutePath)
        }
    }
}

internal fun CodeTemplate.generateMethodCall(
    sprouteKotlinParent: SprouteParent,
    function: SprouteRequestFunction,
    fullRoutePath: String
) {
    when (sprouteKotlinParent) {
        is SprouteClass -> addRouteClassMethodCallCode(sprouteKotlinParent, function)
        is SproutePackage -> generateRoutePackageMethodCall(function, fullRoutePath)
    }
}

internal fun CodeTemplate.generateRoutePackageMethodCall(function: SprouteRequestFunction, fullRoutePath: String) =
    apply {
        when (function.receiver) {
            Route::class.toMemberName() -> addRouteExtensionPackageMethodCallCode(function, fullRoutePath)
            ApplicationCall::class.toMemberName() -> addPackageMethodCallCode(function)
            null -> addPackageMethodCallCode(function)
        }
    }

private fun CodeTemplate.addRouteExtensionPackageMethodCallCode(
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


internal fun CodeTemplate.generateRequestControlFlow(
    function: SprouteRequestFunction,
    generateBody: CodeTemplate.() -> Unit
) {
}

internal fun CodeTemplate.generateCallBlock(function: SprouteRequestFunction, generateBody: CodeTemplate.() -> Unit) {
    if (function.hasReturnValue) {
        generateControlFlowCode(
            "%M.%M",
            KotlinNames.MethodNames.applicationCallGetter,
            KotlinNames.MethodNames.callRespondMethod,
            beginFlowString = "(·",
            endFlowString = "·)·}"
        ) { generateBody() }
    }
    if (function.isApplicationCallExtensionMethod) {
        generateControlFlowCode(
            "%M.%M",
            KotlinNames.MethodNames.applicationCallGetter,
            KotlinNames.MethodNames.applyMethod,
            beginFlowString = "·{·",
            endFlowString = "·}",
        ) { generateBody() }
    }
}

internal fun CodeTemplate.addRouteClassMethodCallCode(
    sprouteKotlinParent: SprouteParent, function: SprouteRequestFunction
) {
    generateCode("%M", sprouteKotlinParent.memberName)
    addMethodParameters((sprouteKotlinParent as SprouteClass).primaryConstructorParams)
    generateCode(".%N", function.simpleName)
    addMethodParameters(function.params)
}


private fun CodeTemplate.addMethodParameters(methodParams: List<MemberName>?) {
    methodParams.ifNotEmpty {
        generateMethodParams(it)
    }.orElse {
        addEmptyMethodParamBrackets()
    }
}

private fun CodeTemplate.generateMethodParams(memberNames: List<MemberName>) {
    val memberParamString = memberNames.joinToString(", ") { "%M" }
    val parameters = "($memberParamString)"
    generateCode(parameters, *memberNames.toTypedArray())
}

private fun CodeTemplate.addEmptyMethodParamBrackets() {
    generateCode("()")
}

internal fun CodeTemplate.addPackageMethodCallCode(function: SprouteRequestFunction) {
    generateCode("%M", function.memberName)
    addMethodParameters(function.params)
}

internal fun CodeTemplate.addRouteExtensionCodeWithoutRoute(function: SprouteRequestFunction) {
    generateCode(
        "%L%N.%M",
        "this@",
        KotlinNames.MethodNames.routeMethod,
        function.memberName
    )
    addMethodParameters(function.params)
}

internal fun CodeTemplate.addRouteExtensionCodeWithRoute(function: SprouteRequestFunction, routeReference: String) {
    generateCode(
        "%L%L.%M",
        "this@",
        "`$routeReference`",
        function.memberName
    )
    addMethodParameters(function.params)
}
