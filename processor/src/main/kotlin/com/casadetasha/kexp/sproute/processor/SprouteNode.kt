package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestFunction

var output: String = ""

internal class SprouteNode(val name: String) {
    val sprouteMap: MutableMap<String, SprouteNode> = HashMap()
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
}

internal data class Bud(val kotlinParent: SprouteKotlinParent, val function: SprouteRequestFunction)

internal fun generateRouteTrie(sprouteParents: Set<SprouteKotlinParent>): SprouteNode {
    val sproutedBuds = SprouteNode("")
    val allBuds: MutableSet<Bud> = HashSet()
    sprouteParents.forEach {
        allBuds.addAll(it.getBuds())
    }

    allBuds.forEach {
        val routeSegments = it.function
            .baseRoutePath
            .trimStart('/')
            .split("/")
        sproutedBuds.addBud(routeSegments, it)
    }
    
    return sproutedBuds
}

//private fun SprouteNode.printRoutes(prefix: String = "") {
//    buds.map { it.function }.forEach {
//        output += "\n${prefix} - ${it.requestMethodName.simpleName.uppercase()}"
//    }
//
//    sprouteMap.values.forEach {
//        it.printRoutes("$prefix/$name")
//    }
//}

private fun SprouteKotlinParent.getBuds(): Set<Bud> = HashSet<Bud>().apply {
    sprouteRequestFunctions.forEach {
        add(Bud(
            kotlinParent = this@getBuds,
            function = it
        ))
    }
}
