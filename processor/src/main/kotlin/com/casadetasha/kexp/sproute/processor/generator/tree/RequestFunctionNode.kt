package com.casadetasha.kexp.sproute.processor.generator.tree

import com.casadetasha.kexp.sproute.processor.sproutes.authentication.Authentication
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteParent
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteRequestFunction

internal data class RequestFunctionNode(
    val kotlinParent: SprouteParent,
    val function: SprouteRequestFunction
    ) : Comparable<RequestFunctionNode> {

    private val fullRoutePath: String = function.fullRoutePath

    val routeSegments by lazy {
        fullRoutePath.trimStart('/')
        .split("/")
    }

    val authentication: Authentication = function.authentication

    override fun compareTo(other: RequestFunctionNode): Int = function.simpleName.compareTo(other.function.simpleName)
}