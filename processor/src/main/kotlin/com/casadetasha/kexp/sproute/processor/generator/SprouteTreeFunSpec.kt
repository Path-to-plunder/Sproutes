package com.casadetasha.kexp.sproute.processor.generator

import com.casadetasha.kexp.sproute.processor.MemberNames
import com.casadetasha.kexp.sproute.processor.SprouteTree
import com.squareup.kotlinpoet.FunSpec
import io.ktor.application.*

internal class SprouteTreeFunSpec(private val sprouteTree: SprouteTree) {

    companion object {
        const val CONFIGURATION_METHOD_SIMPLE_NAME = "configureSproutes"
    }

    val value: FunSpec by lazy {
        FunSpec.builder(CONFIGURATION_METHOD_SIMPLE_NAME)
            .receiver(Application::class)
            .beginControlFlow("%M", MemberNames.MethodNames.routingMethod)
            .amendSproutesFromTree(sprouteTree)
            .endControlFlow()
            .build()
    }

    private fun FunSpec.Builder.amendSproutesFromTree(sprouteTree: SprouteTree): FunSpec.Builder = apply {
        val sortedSprouteMap = sprouteTree.sprouteMap.toSortedMap()
        sortedSprouteMap.forEach {
            amendToFunSpecBuilder(
                authentication = it.key,
                rootNode = it.value
            )

            if (it.key != sortedSprouteMap.lastKey()) addStatement("")
        }
    }
}
