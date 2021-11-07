package com.casadetasha.kexp.sproute.processor.models.sproutes.roots

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

internal object ProcessedSprouteRoots {
    private val sprouteRoots: MutableMap<TypeName, SprouteRoot> by lazy {
        HashMap<TypeName, SprouteRoot>().apply { put(defaultSprouteRoot.childRootKey, defaultSprouteRoot) }
    }

    private val defaultSprouteRoot: SprouteRoot = DefaultSprouteSprouteRoot(Sproute::class.asTypeName())

    fun put(sprouteRoot: SprouteRoot) {
        sprouteRoots[sprouteRoot.childRootKey] = sprouteRoot
    }

    fun putAll(sprouteRootMap: Map<TypeName, SprouteRoot>) {
        sprouteRoots.putAll(sprouteRootMap)
    }

    fun getSprouteRoot(parentRootKey: TypeName?): SprouteRoot {
        return if (parentRootKey == null) {
            defaultSprouteRoot
        } else{
            sprouteRoots[parentRootKey]
                ?: processingEnvironment.printThenThrowError(
                    "Sproute root $parentRootKey not found in sproute roots"
                            + " ( ${getHumanReadableSprouteRootList()} )")
        }
    }

    fun getSprouteRootForChild(parentRootKey: TypeName?, childRootKey: TypeName): SprouteRoot {
        if (parentRootKey == childRootKey) {
            processingEnvironment.printThenThrowError("A Sproute cannot be its own parent")
        }

        return getSprouteRoot(parentRootKey).apply {
            failIfChildRootIsCyclical(childRootKey)
        }
    }

    private fun getHumanReadableSprouteRootList() = sprouteRoots.values
        .map { it.childRootKey }
        .joinToString(", ")
}
