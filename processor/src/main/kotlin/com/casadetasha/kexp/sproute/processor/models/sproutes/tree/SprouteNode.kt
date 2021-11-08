package com.casadetasha.kexp.sproute.processor.models.sproutes.tree

import com.casadetasha.kexp.sproute.processor.ktx.removeFirst
import java.util.*

internal class SprouteNode(val name: String): Comparable<SprouteNode> {
    private val sprouteMap: MutableMap<String, SprouteNode> = HashMap()
    val sproutes: SortedSet<SprouteNode> get() { return sprouteMap.values.toSortedSet() }

    private val requestFunctionNodes: MutableSet<RequestFunctionNode> = HashSet()
    val sortedRequestFunctionNodes: Set<RequestFunctionNode> get() = requestFunctionNodes.toSortedSet()

    fun addBud(routeSegments: List<String>, requestFunctionNode: RequestFunctionNode) {
        if (routeSegments.isEmpty()) {
            requestFunctionNodes.add(requestFunctionNode)
            return
        }

        moveToNextNode(routeSegments.first(), routeSegments.removeFirst(), requestFunctionNode)
    }

    private fun moveToNextNode(nextSegment: String, pendingSegments: List<String>, requestFunctionNode: RequestFunctionNode) {
        createNodeIfNotPresent(nextSegment)
        sprouteMap[nextSegment]!!.addBud(pendingSegments, requestFunctionNode)
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
