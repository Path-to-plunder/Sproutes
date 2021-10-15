package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf

internal data class SprouteRootInfo(
    val packageName: String,
    val routeSegment: String,
    val canAppendPackage: Boolean,
    val sprouteAuthentication: SprouteAuthentication
) {

    fun getPathPrefixToSproutePackage(sproutePackage: String): String {
        return if (canAppendPackage) {
            routeSegment + sproutePackage.asSubPackageOf(packageName).asPath().lowercase()
        } else routeSegment
    }
}
