package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.processor.ktx.removeFirst
import java.util.*

internal class SprouteNode(val name: String): Comparable<SprouteNode> {
    private val sprouteMap: MutableMap<String, SprouteNode> = HashMap()
    val sproutes: SortedSet<SprouteNode> get() { return sprouteMap.values.toSortedSet() }

    private val buds: MutableSet<Bud> = HashSet()
    val sortedBuds: Set<Bud> get() = buds.toSortedSet()

    fun addBud(routeSegments: List<String>, bud: Bud) {
        if (routeSegments.isEmpty()) {
            buds.add(bud)
            return
        }

        moveToNextNode(routeSegments.first(), routeSegments.removeFirst(), bud)
    }

    private fun moveToNextNode(nextSegment: String, pendingSegments: List<String>, bud: Bud) {
        createNodeIfNotPresent(nextSegment)
        sprouteMap[nextSegment]!!.addBud(pendingSegments, bud)
    }

    private fun createNodeIfNotPresent(segmentName: String) {
        if (sprouteMap[segmentName] == null) {
            sprouteMap[segmentName] = SprouteNode(segmentName)
        }
    }

    override fun compareTo(other: SprouteNode): Int {
        return this.name.compareTo(other.name)
    }
}
