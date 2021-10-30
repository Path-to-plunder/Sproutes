package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.Bud.Companion.generateBuds
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import java.util.*

internal class SprouteNode(val name: String): Comparable<SprouteNode> {
    private val sprouteMap: MutableMap<String, SprouteNode> = HashMap()
    val sproutes: SortedSet<SprouteNode> get() { return sprouteMap.values.toSortedSet() }

    private val budsField: MutableSet<Bud> = HashSet()
    val buds: Set<Bud> get() = budsField.toSortedSet()

    companion object {
        internal fun generateSproutNodes(sprouteParents: Set<SprouteKotlinParent>): SprouteNode {
            val sproutedBuds = SprouteNode("")
            val allBuds: MutableSet<Bud> = HashSet()
            sprouteParents.forEach {
                allBuds.addAll(generateBuds(it))
            }

            allBuds.forEach {
                val routeSegments = it.function
                    .fullRoutePath
                    .trimStart('/')
                    .split("/")
                sproutedBuds.addBud(routeSegments, it)
            }

            return sproutedBuds
        }
    }

    fun addBud(routeSegments: List<String>, bud: Bud) {
        if (routeSegments.isEmpty()) {
            budsField.add(bud)
            return
        }

        val segmentName: String = routeSegments.first()
        if (sprouteMap[segmentName] == null) {
            sprouteMap[segmentName] = SprouteNode(segmentName)
        }

        val trimmedSegments = routeSegments.toMutableList().apply { removeFirst() }
        sprouteMap[segmentName]!!.addBud(trimmedSegments, bud)
    }

    override fun compareTo(other: SprouteNode): Int {
        return this.name.compareTo(other.name)
    }
}
