package com.casadetasha.kexp.sproute.processor.models.sproutes.tree

import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteAuthentication
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteParent

internal class SprouteTree private constructor(val sprouteMap: Map<SprouteAuthentication, SprouteNode>) {

    class LazyLoader(
        private val kotlinParents: Set<SprouteParent>
    ) {

        val value: SprouteTree by lazy { SprouteTree(roots) }

        private val httpRequestNodes: Set<HttpRequestNode> by lazy {
            HashSet<HttpRequestNode>().apply {
                kotlinParents.forEach {
                    addAll(generateBuds(it))
                }
            }.toSet()
        }

        private val roots: Map<SprouteAuthentication, SprouteNode> by lazy {
            HashMap<SprouteAuthentication, SprouteNode>().apply {
                httpRequestNodes.forEach {
                    val key = it.authentication
                    if (this[key] == null) {
                        this[key] = SprouteNode("")
                    }

                    this[key]!!.addBud(it.routeSegments, it)
                }
            }.toMap()
        }

        private fun generateBuds(sprouteKotlinParent: SprouteParent): Set<HttpRequestNode> = java.util.HashSet<HttpRequestNode>().apply {
            sprouteKotlinParent.sprouteRequestFunctions.forEach {
                add(
                    HttpRequestNode(
                        kotlinParent = sprouteKotlinParent,
                        function = it
                    )
                )
            }
        }
    }
}
