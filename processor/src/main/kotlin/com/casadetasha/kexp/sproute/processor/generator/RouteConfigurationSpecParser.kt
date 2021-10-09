package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.FunSpec
import io.ktor.application.*

internal class RouteConfigurationSpecParser(private val sprouteKotlinParent: ImmutableSet<SprouteKotlinParent>) {

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
        sprouteKotlinParent.forEach { addStatement( "%M()", it.configurationMethodName ) }
    }
}
