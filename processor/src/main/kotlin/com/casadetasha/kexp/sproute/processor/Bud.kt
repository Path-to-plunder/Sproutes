package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestFunction
import java.util.HashSet

internal data class Bud(
    val kotlinParent: SprouteKotlinParent,
    val function: SprouteRequestFunction
    ) : Comparable<Bud> {

    companion object {
        internal fun generateBuds(sprouteKotlinParent: SprouteKotlinParent): Set<Bud> = HashSet<Bud>().apply {
            sprouteKotlinParent.sprouteRequestFunctions.forEach {
                add(Bud(
                    kotlinParent = sprouteKotlinParent,
                    function = it
                ))
            }
        }
    }

    override fun compareTo(other: Bud): Int = function.simpleName.compareTo(other.function.simpleName)
}