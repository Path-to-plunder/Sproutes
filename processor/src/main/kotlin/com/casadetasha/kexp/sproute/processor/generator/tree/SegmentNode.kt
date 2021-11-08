package com.casadetasha.kexp.sproute.processor.generator.tree

import com.casadetasha.kexp.sproute.processor.ktx.removeFirst
import java.util.*

internal class SegmentNode(val name: String): Comparable<SegmentNode> {
    private val sprouteMap: MutableMap<String, SegmentNode> = HashMap()
    val sproutes: SortedSet<SegmentNode> get() { return sprouteMap.values.toSortedSet() }

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
            sprouteMap[segmentName] = SegmentNode(segmentName)
        }
    }

    override fun compareTo(other: SegmentNode): Int {
        return this.name.compareTo(other.name)
    }
}
