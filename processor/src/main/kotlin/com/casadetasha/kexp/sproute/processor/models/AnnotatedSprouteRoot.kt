package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.squareup.kotlinpoet.TypeName

internal interface Root {
    companion object {
        internal val sprouteRoots: MutableMap<TypeName, Root> = HashMap()
        internal lateinit var defaultRoot: Root
    }

    val rootKey: TypeName
    val sprouteAuthentication: SprouteAuthentication
    fun getSproutePathForPackage(sproutePackage: String): String
}

internal class AnnotatedSprouteRoot(
    override val rootKey: TypeName,
    val packageName: String,
    val routeSegment: String,
    val canAppendPackage: Boolean,
    override val sprouteAuthentication: SprouteAuthentication
): Root {
    override fun getSproutePathForPackage(sproutePackage: String): String {
        return if (canAppendPackage) {
            routeSegment + sproutePackage.asSubPackageOf(packageName).asPath().lowercase()
        } else routeSegment
    }
}

internal class DefaultSprouteRoot(override val rootKey: TypeName): Root {
    override val sprouteAuthentication: SprouteAuthentication = SprouteAuthentication.BaseAuthentication()
    override fun getSproutePathForPackage(sproutePackage: String) = ""
}
