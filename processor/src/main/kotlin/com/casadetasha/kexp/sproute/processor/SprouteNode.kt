package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestFunction
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

internal class SprouteNode(val name: String): Comparable<SprouteNode> {
    private val sprouteMap: MutableMap<String, SprouteNode> = HashMap()
    val sproutes: SortedSet<SprouteNode> get() {return sprouteMap.values.toSortedSet() }
    val buds: MutableSet<Bud> = HashSet()

    fun addBud(routeSegments: List<String>, bud: Bud) {
        if (routeSegments.isEmpty()) {
            buds.add(bud)
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

internal data class Bud(val kotlinParent: SprouteKotlinParent, val function: SprouteRequestFunction)

internal fun generateSproutNodes(sprouteParents: Set<SprouteKotlinParent>): SprouteNode {
    val sproutedBuds = SprouteNode("")
    val allBuds: MutableSet<Bud> = HashSet()
    sprouteParents.forEach {
        allBuds.addAll(it.getBuds())
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

private fun SprouteKotlinParent.getBuds(): Set<Bud> = HashSet<Bud>().apply {
    sprouteRequestFunctions.forEach {
        add(Bud(
            kotlinParent = this@getBuds,
            function = it
        ))
    }
}
