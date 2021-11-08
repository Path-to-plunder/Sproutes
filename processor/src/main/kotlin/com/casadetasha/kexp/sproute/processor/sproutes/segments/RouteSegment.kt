package com.casadetasha.kexp.sproute.processor.sproutes.segments

import com.casadetasha.kexp.sproute.processor.sproutes.authentication.Authentication
import com.squareup.kotlinpoet.TypeName

internal interface RouteSegment {
    val segmentKey: TypeName
    val authentication: Authentication
    fun getSproutePathForPackage(sproutePackage: String): String
    fun failIfChildSegmentIsCyclical(childSegmentKeys: List<TypeName>)
}
