package com.casadetasha.kexp.sproute.processor.models.sproutes.tree

import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.Authentication
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteParent

internal class SprouteTree private constructor(val sprouteMap: Map<Authentication, SprouteNode>) {

    class LazyLoader(
        private val kotlinParents: Set<SprouteParent>
    ) {

        val value: SprouteTree by lazy { SprouteTree(nodeMap) }

        private val requestFunctionNodes: Set<RequestFunctionNode> by lazy {
            HashSet<RequestFunctionNode>().apply {
                kotlinParents.forEach {
                    addAll(generateBuds(it))
                }
            }.toSet()
        }

        private val nodeMap: Map<Authentication, SprouteNode> by lazy {
            HashMap<Authentication, SprouteNode>().apply {
                requestFunctionNodes.forEach {
                    val key = it.authentication
                    if (this[key] == null) {
                        this[key] = SprouteNode("")
                    }

                    this[key]!!.addBud(it.routeSegments, it)
                }
            }.toMap()
        }

        private fun generateBuds(sprouteKotlinParent: SprouteParent): Set<RequestFunctionNode> = java.util.HashSet<RequestFunctionNode>().apply {
            sprouteKotlinParent.sprouteRequestFunctions.forEach {
                add(
                    RequestFunctionNode(
                        kotlinParent = sprouteKotlinParent,
                        function = it
                    )
                )
            }
        }
    }
}
