package com.casadetasha.kexp.sproute.processor.sproutes.segments

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

internal object ProcessedRouteSegments {
    private val routeSegments: MutableMap<TypeName, RouteSegment> by lazy {
        HashMap<TypeName, RouteSegment>().apply { put(defaultRouteSegment.segmentKey, defaultRouteSegment) }
    }

    private val defaultRouteSegment: RouteSegment =
        LeadingRouteSegment.DefaultRouteSegment(Sproute::class.asTypeName())
    val defaultSegmentKey: TypeName = defaultRouteSegment.segmentKey

    fun put(routeSegment: RouteSegment) {
        routeSegments[routeSegment.segmentKey] = routeSegment
    }

    fun putAll(routeSegmentMap: Map<TypeName, RouteSegment>) {
        routeSegments.putAll(routeSegmentMap)
    }

    fun getSprouteRoot(parentSegmentKey: TypeName?): RouteSegment {
        return if (parentSegmentKey == null) {
            defaultRouteSegment
        } else{
            routeSegments[parentSegmentKey]
                ?: processingEnvironment.printThenThrowError(
                    "Sproute root $parentSegmentKey not found in sproute roots"
                            + " ( ${getHumanReadableSprouteRootList()} )")
        }
    }

    fun getSprouteRootForSegment(parentSegmentKey: TypeName?, childSegmentKeys: List<TypeName>): RouteSegment {
        return getSprouteRoot(parentSegmentKey).apply {
            failIfChildSegmentIsCyclical(childSegmentKeys)
        }
    }

    private fun getHumanReadableSprouteRootList() = routeSegments.values
        .map { it.segmentKey }
        .joinToString(", ")
}
