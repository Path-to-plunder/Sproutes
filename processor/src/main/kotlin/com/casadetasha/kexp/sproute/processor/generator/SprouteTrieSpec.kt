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
import io.ktor.application.*
import io.ktor.routing.*

internal class SprouteTrieSpec(private val rootNode: SprouteNode) {
    private lateinit var funBuilder: FunSpec.Builder

    val funSpec: FunSpec by lazy {
        funBuilder = FunSpec.builder("sprouteBuds").receiver(Application::class)
        funBuilder.beginControlFlow("%M", MethodNames.routingMethod)

        amendFunForNode(rootNode)

        funBuilder.endControlFlow()
        funBuilder.build()
    }

    private fun amendFunForNode(node: SprouteNode) {
        beginNodeControlFlow(node.name)                         // route("/routeSegment") {
        node.buds.forEach { amendFunForBud(it) }                //   ...
        node.sprouteMap.values.forEach { amendFunForNode(it) }  //   ...
        funBuilder.endControlFlow()                             // }
    }

    private fun beginNodeControlFlow(routeSegment: String) {
        funBuilder.beginControlFlow(
            "%M(%S)",
            MethodNames.routeMethod,
            "/$routeSegment"
        )
    }

    private fun amendFunForBud(bud: Bud) {
        beginRequestControlFlow(bud.function)          //     get ("/path") {
        beginCallControlFlow(bud.function)             //       call.respond(
        addMethodCall(bud.kotlinParent, bud.function)  //         Route().get()
        endCallControlFlow(bud.function)               //       )
        endRequestControlFlow()                        //     }
    }

    private fun beginRequestControlFlow(function: SprouteRequestFunction) = apply {
        if (function.functionPathSegment.isBlank()) {
            funBuilder.beginControlFlow(
                "%M() ",
                function.requestMethodName
            )
        } else {
            funBuilder.beginControlFlow(
                "%M(%S) ",
                function.requestMethodName,
                function.functionPathSegment,
            )
        }
    }

    private fun beginCallControlFlow(function: SprouteRequestFunction) = apply {
        if (function.hasReturnValue) {
            funBuilder.addCode(
                "%M.%M(",
                MethodNames.applicationCallGetter,
                MethodNames.callRespondMethod,
            )
        }
        if (function.isApplicationCallExtensionMethod) {
            funBuilder.beginControlFlow(
                "%M.%M",
                MethodNames.applicationCallGetter,
                MethodNames.applyMethod
            )
        }
    }

    private fun addRoutePackageMethodCall(function: SprouteRequestFunction) = apply {
        when (function.receiver) {
            Route::class.toMemberName() -> addRouteExtensionPackageMethodCall(function)
            ApplicationCall::class.toMemberName() -> addPackageMethodCall(function)
            null -> addPackageMethodCall(function)
        }
    }

    private fun addRouteExtensionPackageMethodCall(function: SprouteRequestFunction) {
        funBuilder.addCode(
            CodeBlock.builder()
                .add(
                    "  %L%N.%M",
                    "this@",
                    MethodNames.routeMethod,
                    function.memberName
                )
                .addMethodParameters(function.params)
                .build()
        )
    }

    private fun addMethodCall(
        sprouteKotlinParent: SprouteKotlinParent, function: SprouteRequestFunction
    ) = apply {
        when (sprouteKotlinParent) {
            is SprouteKotlinParent.SprouteClass -> addRouteClassMethodCall(sprouteKotlinParent, function)
            is SprouteKotlinParent.SproutePackage -> addRoutePackageMethodCall(function)
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

    private fun endCallControlFlow(function: SprouteRequestFunction) = apply {
        if (function.isApplicationCallExtensionMethod) {
            funBuilder.addStatement("")
                .endControlFlow()
        }
        if (function.hasReturnValue) {
            funBuilder.addStatement(")")
        } else {
            funBuilder.addStatement("")
        }
    }

    private fun endRequestControlFlow() = apply { funBuilder.endControlFlow() }
}
