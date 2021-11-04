package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteKotlinParent

internal class SprouteTree private constructor(val sprouteMap: Map<SprouteAuthentication, SprouteNode>) {

    class LazyLoader(
        private val kotlinParents: Set<SprouteKotlinParent>
    ) {

        val value: SprouteTree by lazy { SprouteTree(roots) }

        private val buds: Set<Bud> by lazy {
            HashSet<Bud>().apply {
                kotlinParents.forEach {
                    addAll(generateBuds(it))
                }
            }.toSet()
        }

        private val roots: Map<SprouteAuthentication, SprouteNode> by lazy {
            HashMap<SprouteAuthentication, SprouteNode>().apply {
                buds.forEach {
                    val key = it.authentication
                    if (this[key] == null) {
                        this[key] = SprouteNode("")
                    }

                    this[key]!!.addBud(it.routeSegments, it)
                }
            }.toMap()
        }

        private fun generateBuds(sprouteKotlinParent: SprouteKotlinParent): Set<Bud> = java.util.HashSet<Bud>().apply {
            sprouteKotlinParent.sprouteRequestFunctions.forEach {
                add(
                    Bud(
                        kotlinParent = sprouteKotlinParent,
                        function = it
                    )
                )
            }
        }
    }
}
