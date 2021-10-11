package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.MemberNames.MethodNames
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import io.ktor.application.*

internal class RouteFileSpecParser(private val sprouteKotlinParent: SprouteKotlinParent) {

    val configurationFunSpec: FunSpec by lazy {
        FunSpec.builder(sprouteKotlinParent.configurationMethodSimpleName)
            .addModifiers(KModifier.INTERNAL)
            .receiver(Application::class)
            .beginControlFlow("%M", MethodNames.routingMethod)
            .addRouteMethods()
            .endControlFlow()
            .build()
    }

    private fun FunSpec.Builder.addRouteMethods(): FunSpec.Builder = apply {
        sprouteKotlinParent.requestFunctions
            .map { RequestFunSpecParser(sprouteKotlinParent, it).funSpec }
            .forEach { addStatement("%N()", it) }
    }
}
