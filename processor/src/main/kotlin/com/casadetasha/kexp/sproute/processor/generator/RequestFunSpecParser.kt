package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.MemberNames.MethodNames
import com.casadetasha.kexp.sproute.processor.ktx.addMethodParameters
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.models.RequestFunction
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent.SprouteClass
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent.SproutePackage
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.ktor.application.*
import io.ktor.routing.*

@OptIn(KotlinPoetMetadataPreview::class)
internal class RequestFunSpecParser(
    private val sprouteKotlinParent: SprouteKotlinParent,
    private val requestFunction: RequestFunction
) {

    val funSpec: FunSpec by lazy {
        FunSpec.builder(requestFunction.configurationMethodSimpleName)
            .addModifiers(KModifier.INTERNAL)
            .receiver(Route::class)                 // internal Route.configurationMethodSimpleName() {
            .beginSetAuthenticationRequirements()   //   authenticate("auth configuration") {
            .beginRequestControlFlow()              //     get ("/path") {
            .beginCallControlFlow()                 //       call.respond(
            .addMethodCall()                        //         Route().get()
            .endCallControlFlow()                   //       )
            .endRequestControlFlow()                //     }
            .endSetAuthenticationRequirements()     //   }
            .build()
    }

    private fun FunSpec.Builder.beginSetAuthenticationRequirements() = apply {
        if (requestFunction.isAuthenticationRequested && requestFunction.hasAuthenticationParams) {
            beginControlFlow(
                "%M(%L) ",
                MethodNames.authenticationScopeMethod,
                requestFunction.authenticationParams
            )
        } else if (requestFunction.isAuthenticationRequested) {
            beginControlFlow("%M() ", MethodNames.authenticationScopeMethod)
        }
    }

    private fun FunSpec.Builder.beginRequestControlFlow() = apply {
        beginControlFlow(
            "%M(%S) ",
            requestFunction.requestMethodName,
            requestFunction.fullRoutePath,
        )
    }

    private fun FunSpec.Builder.endRequestControlFlow() = apply { endControlFlow() }

    private fun FunSpec.Builder.endSetAuthenticationRequirements() = apply {
        if (requestFunction.isAuthenticationRequested) {
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.beginCallControlFlow() = apply {
        if (requestFunction.hasReturnValue) {
            addStatement(
                "%M.%M(",
                MethodNames.applicationCallGetter,
                MethodNames.callRespondMethod,
            )
        }
        if (requestFunction.isApplicationCallExtensionMethod) {
            beginControlFlow(
                "%M.%M",
                MethodNames.applicationCallGetter,
                MethodNames.applyMethod
            )
        }
    }

    private fun FunSpec.Builder.addMethodCall() = apply {
        when(sprouteKotlinParent) {
            is SprouteClass -> addRouteClassMethodCall()
            is SproutePackage -> addRoutePackageMethodCall()
        }
    }

    private fun FunSpec.Builder.addRouteClassMethodCall() = apply {
        addCode(
            CodeBlock.builder()
                .add("  %M", sprouteKotlinParent.memberName)
                .addMethodParameters((sprouteKotlinParent as SprouteClass).primaryConstructorParams)
                .add(".%N", requestFunction.simpleName)
                .addMethodParameters(requestFunction.functionParams)
                .add("\n")
                .build()
        )
    }

    private fun FunSpec.Builder.addRoutePackageMethodCall() = apply {
        when(requestFunction.functionReceiver) {
            Route::class.toMemberName() -> addRouteExtensionPackageMethodCall()
            ApplicationCall::class.toMemberName() -> addPackageMethodCall()
            null -> addPackageMethodCall()
        }
    }

    private fun FunSpec.Builder.addRouteExtensionPackageMethodCall() {
        addCode(
            CodeBlock.builder()
                .add("  %L%N.%M",
                    "this@",
                    requestFunction.configurationMethodSimpleName,
                    requestFunction.memberName)
                .addMethodParameters(requestFunction.functionParams)
                .add("\n")
                .build()
        )
    }

    private fun FunSpec.Builder.addPackageMethodCall() {
        addCode(
            CodeBlock.builder()
                .add("  %M", requestFunction.memberName)
                .addMethodParameters(requestFunction.functionParams)
                .add("\n")
                .build()
        )
    }

    private fun FunSpec.Builder.endCallControlFlow() = apply {
        if (requestFunction.isApplicationCallExtensionMethod) {
            endControlFlow()
        }
        if (requestFunction.hasReturnValue) {
            addStatement(")")
        }
    }
}
