package com.casadetasha.kexp.sproute.processor.sproutes.segments

import com.casadetasha.kexp.annotationparser.AnnotationParser.printThenThrowError
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.AuthLazyLoader
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.Authentication
import com.squareup.kotlinpoet.TypeName

internal class TrailingRouteSegment(
    override val segmentKey: TypeName,
    val parentSegmentKey: TypeName,
    val routeSegment: String,
    authLazyLoader: AuthLazyLoader
): RouteSegment {

    private var parentSegmentValue: RouteSegment? = null

    private fun getParentSegment(childSegments: List<TypeName>): RouteSegment {
        parentSegmentValue = parentSegmentValue
            ?: ProcessedRouteSegments.getSprouteRootForSegment(
                parentSegmentKey,
                childSegments
            )

        return parentSegmentValue!!
    }

    override val authentication: Authentication by lazy { authLazyLoader.value }

    override fun getSproutePathForPackage(sproutePackage: String): String {
        val parentSegments = getParentSegment(listOf(segmentKey))
            .getSproutePathForPackage(sproutePackage)
        return "${parentSegments}$routeSegment"
    }

    override fun failIfChildSegmentIsCyclical(childSegmentKeys: List<TypeName>) {
        if (childSegmentKeys.contains(segmentKey)) {
            printThenThrowError("Found cyclical sprouteRoot dependency for Sproute ($segmentKey)")
        }

        getParentSegment(childSegmentKeys).failIfChildSegmentIsCyclical(childSegmentKeys + segmentKey)
    }
}
