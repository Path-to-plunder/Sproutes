package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteParent
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteRequestFunction

internal data class Bud(
    val kotlinParent: SprouteParent,
    val function: SprouteRequestFunction
    ) : Comparable<Bud> {

    private val fullRoutePath: String = function.fullRoutePath

    val routeSegments by lazy {
        fullRoutePath.trimStart('/')
        .split("/")
    }

    val authentication: SprouteAuthentication = function.sprouteAuthentication

    override fun compareTo(other: Bud): Int = function.simpleName.compareTo(other.function.simpleName)
}