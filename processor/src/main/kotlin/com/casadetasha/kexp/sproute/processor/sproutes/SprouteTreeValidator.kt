package com.casadetasha.kexp.sproute.processor.sproutes

import com.casadetasha.kexp.annotationparser.AnnotationParser.printThenThrowError
import com.casadetasha.kexp.sproute.processor.generator.tree.SegmentNode
import com.casadetasha.kexp.sproute.processor.generator.tree.SprouteTree

internal class SprouteTreeValidator(val sprouteTree: SprouteTree) {

    private val rootSprouteSegments = sprouteTree.sprouteMap.values
    private val routeCountMap = HashMap<String, Int>()

    fun checkForDuplicateRoutes() {
        rootSprouteSegments.forEach {
            addSegmentRouteCounts(it)
        }

        val duplicateRoutesMessage = routeCountMap.filterValues { it > 1 }
            .map { "${it.key} found ${it.value} times"  }
            .joinToString(", ")

        if (duplicateRoutesMessage.isNotBlank()) {
            printThenThrowError("Duplicate request/routes permutations found. Routes must be unique per" +
                    " request type. Duplicate routes: $duplicateRoutesMessage ")
        }
    }

    private fun addSegmentRouteCounts(routeSegment: SegmentNode) {
        routeSegment.sortedRequestFunctionNodes.forEach {
            countRoute(it.function.requestMethodName.simpleName + " " + it.function.fullRoutePath)
        }

        routeSegment.sproutes.forEach {
            addSegmentRouteCounts(it)
        }
    }

    private fun countRoute(rawRoute: String) {
        val route = rawRoute.replace(paramRegex, "{param}")
        val currentRouteCount = routeCountMap[route]
        if (currentRouteCount == null) routeCountMap[route] = 1 else routeCountMap[route] = currentRouteCount + 1
    }

    companion object {
        private val paramRegex = "\\{[^}]+}".toRegex() // Match all sequences of {any-value}
    }
}
