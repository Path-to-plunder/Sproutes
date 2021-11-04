package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.squareup.kotlinpoet.TypeName

internal data class SprouteRootInfo(
    val packageName: String,
    val routeSegment: String,
    val canAppendPackage: Boolean,
    val sprouteAuthentication: SprouteAuthentication
) {

    companion object {
        internal val sprouteRoots: MutableMap<TypeName, SprouteRootInfo> = HashMap()
    }


    fun getPathPrefixToSproutePackage(sproutePackage: String): String {
        return if (canAppendPackage) {
            routeSegment + sproutePackage.asSubPackageOf(packageName).asPath().lowercase()
        } else routeSegment
    }
}
