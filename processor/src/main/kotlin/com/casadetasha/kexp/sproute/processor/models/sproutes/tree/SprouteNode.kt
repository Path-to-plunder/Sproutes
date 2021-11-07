package com.casadetasha.kexp.sproute.processor.models.sproutes.tree

import com.casadetasha.kexp.sproute.processor.ktx.removeFirst
import java.util.*

internal class SprouteNode(val name: String): Comparable<SprouteNode> {
    private val sprouteMap: MutableMap<String, SprouteNode> = HashMap()
    val sproutes: SortedSet<SprouteNode> get() { return sprouteMap.values.toSortedSet() }

    private val httpRequestNodes: MutableSet<HttpRequestNode> = HashSet()
    val sortedHttpRequestNodes: Set<HttpRequestNode> get() = httpRequestNodes.toSortedSet()

    fun addBud(routeSegments: List<String>, httpRequestNode: HttpRequestNode) {
        if (routeSegments.isEmpty()) {
            httpRequestNodes.add(httpRequestNode)
            return
        }

        moveToNextNode(routeSegments.first(), routeSegments.removeFirst(), httpRequestNode)
    }

    private fun moveToNextNode(nextSegment: String, pendingSegments: List<String>, httpRequestNode: HttpRequestNode) {
        createNodeIfNotPresent(nextSegment)
        sprouteMap[nextSegment]!!.addBud(pendingSegments, httpRequestNode)
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
