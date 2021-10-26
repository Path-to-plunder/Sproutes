package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.generator.FileGenerator.Companion.ROUTING_PACKAGE_NAME
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import io.ktor.application.*

internal class RouteConfigurationSpecParser {

    companion object {
        const val CONFIGURATION_METHOD_SIMPLE_NAME = "configureSproutes"
    }

    val routeConfigurationFunSpec: FunSpec by lazy {
        FunSpec.builder(CONFIGURATION_METHOD_SIMPLE_NAME)
            .receiver(Application::class)
            .addRouteConfigurationCalls()
            .build()
    }

    private fun FunSpec.Builder.addRouteConfigurationCalls() = apply {
        addStatement("%M()", MemberName(ROUTING_PACKAGE_NAME, "sprouteBuds"))
    }
}
