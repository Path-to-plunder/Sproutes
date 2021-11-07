package com.casadetasha.kexp.sproute.processor.models.sproutes

import com.squareup.kotlinpoet.MemberName

internal sealed class SprouteParent(
    val packageName: String,
    val classSimpleName: String
) : Comparable<SprouteParent> {

    val memberName = MemberName(packageName, classSimpleName)

    abstract val sprouteRequestFunctions: Set<SprouteRequestFunction>

    override fun compareTo(other: SprouteParent): Int {
        return this.memberName.toString().compareTo(other.memberName.toString())
    }
}
