package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.squareup.kotlinpoet.TypeName

internal interface Root {
    companion object {
        internal val sprouteRoots: MutableMap<String, Root> = HashMap()
        internal lateinit var defaultRoot: Root
    }
    val key: String
    val sprouteAuthentication: SprouteAuthentication

    fun getSproutePathForPackage(sproutePackage: String): String
}

internal class AnnotatedSprouteRoot(
    typeName: TypeName,
    val packageName: String,
    val routeSegment: String,
    val canAppendPackage: Boolean,
    override val sprouteAuthentication: SprouteAuthentication
): Root {
    override val key: String = typeName.toString()

    override fun getSproutePathForPackage(sproutePackage: String): String {
        return if (canAppendPackage) {
            routeSegment + sproutePackage.asSubPackageOf(packageName).asPath().lowercase()
        } else routeSegment
    }
}
