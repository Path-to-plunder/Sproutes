package com.casadetasha.kexp.sproute.processor.models.sproutes.roots

import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.Authentication
import com.squareup.kotlinpoet.TypeName

internal interface SprouteSegment {
    val segmentKey: TypeName
    val authentication: Authentication
    fun getSproutePathForPackage(sproutePackage: String): String
    fun failIfChildRootIsCyclical(childRootKey: TypeName)
}
