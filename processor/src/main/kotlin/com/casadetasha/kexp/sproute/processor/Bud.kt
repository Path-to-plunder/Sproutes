package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.models.SprouteAuthentication
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestFunction

internal data class Bud(
    val kotlinParent: SprouteKotlinParent,
    val function: SprouteRequestFunction
    ) : Comparable<Bud> {

    private val fullRoutePath: String = function.fullRoutePath

    val routeSegments by lazy {
        fullRoutePath.trimStart('/')
        .split("/")
    }

    val authentication: SprouteAuthentication = function.authentication

    override fun compareTo(other: Bud): Int = function.simpleName.compareTo(other.function.simpleName)
}