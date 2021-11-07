package com.casadetasha.kexp.sproute.processor.models.sproutes.tree

import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteAuthentication
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteParent
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteRequestFunction

internal data class HttpRequestNode(
    val kotlinParent: SprouteParent,
    val function: SprouteRequestFunction
    ) : Comparable<HttpRequestNode> {

    private val fullRoutePath: String = function.fullRoutePath

    val routeSegments by lazy {
        fullRoutePath.trimStart('/')
        .split("/")
    }

    val authentication: SprouteAuthentication = function.sprouteAuthentication

    override fun compareTo(other: HttpRequestNode): Int = function.simpleName.compareTo(other.function.simpleName)
}