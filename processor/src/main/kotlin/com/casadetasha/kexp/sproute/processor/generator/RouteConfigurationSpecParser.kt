package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.MemberNames
import com.casadetasha.kexp.sproute.processor.SprouteTree
import com.squareup.kotlinpoet.FunSpec
import io.ktor.application.*

internal class RouteConfigurationSpecParser(private val sprouteTree: SprouteTree) {

    companion object {
        const val CONFIGURATION_METHOD_SIMPLE_NAME = "configureSproutes"
    }

    val routeConfigurationFunSpec: FunSpec by lazy {
        var funBuilder: FunSpec.Builder = FunSpec.builder(CONFIGURATION_METHOD_SIMPLE_NAME)
            .receiver(Application::class)
            .beginControlFlow("%M", MemberNames.MethodNames.routingMethod)

        sprouteTree.sprouteMap.toSortedMap().forEach {
            funBuilder = SprouteSpec(
                authentication = it.key,
                rootNode = it.value
            ).amendToFunSpecBuilder(funBuilder)

            funBuilder.addStatement("")
        }

        funBuilder.endControlFlow().build()
    }
}
