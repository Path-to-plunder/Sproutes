package com.casadetasha.kexp.sproute.processor.models.sproutes.roots

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

internal object ProcessedSprouteSegments {
    private val sprouteSegments: MutableMap<TypeName, SprouteSegment> by lazy {
        HashMap<TypeName, SprouteSegment>().apply { put(defaultSprouteSegment.segmentKey, defaultSprouteSegment) }
    }

    private val defaultSprouteSegment: SprouteSegment = DefaultSprouteSprouteSegment(Sproute::class.asTypeName())
    val defaultSegmentKey: TypeName = defaultSprouteSegment.segmentKey

    fun put(sprouteSegment: SprouteSegment) {
        sprouteSegments[sprouteSegment.segmentKey] = sprouteSegment
    }

    fun putAll(sprouteSegmentMap: Map<TypeName, SprouteSegment>) {
        sprouteSegments.putAll(sprouteSegmentMap)
    }

    fun getSprouteRoot(parentRootKey: TypeName?): SprouteSegment {
        return if (parentRootKey == null) {
            defaultSprouteSegment
        } else{
            sprouteSegments[parentRootKey]
                ?: processingEnvironment.printThenThrowError(
                    "Sproute root $parentRootKey not found in sproute roots"
                            + " ( ${getHumanReadableSprouteRootList()} )")
        }
    }

    fun getSprouteRootForChild(parentRootKey: TypeName?, childRootKey: TypeName): SprouteSegment {
        if (parentRootKey == childRootKey) {
            processingEnvironment.printThenThrowError("A Sproute cannot be its own parent")
        }

        return getSprouteRoot(parentRootKey).apply {
            failIfChildRootIsCyclical(childRootKey)
        }
    }

    private fun getHumanReadableSprouteRootList() = sprouteSegments.values
        .map { it.segmentKey }
        .joinToString(", ")

}
