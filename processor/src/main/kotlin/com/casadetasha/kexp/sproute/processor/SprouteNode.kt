package com.casadetasha.kexp.sproute.processor

import java.util.*

internal class SprouteNode(val name: String): Comparable<SprouteNode> {
    private val sprouteMap: MutableMap<String, SprouteNode> = HashMap()
    val sproutes: SortedSet<SprouteNode> get() { return sprouteMap.values.toSortedSet() }

    private val budsField: MutableSet<Bud> = HashSet()
    val buds: Set<Bud> get() = budsField.toSortedSet()

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
