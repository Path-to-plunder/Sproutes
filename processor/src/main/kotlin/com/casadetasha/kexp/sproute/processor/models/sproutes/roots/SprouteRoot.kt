package com.casadetasha.kexp.sproute.processor.models.sproutes.roots

import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteAuthentication
import com.squareup.kotlinpoet.TypeName

internal interface SprouteRoot {
    val childRootKey: TypeName
    val sprouteAuthentication: SprouteAuthentication
    fun getSproutePathForPackage(sproutePackage: String): String
    fun failIfChildRootIsCyclical(childRootKey: TypeName)
}