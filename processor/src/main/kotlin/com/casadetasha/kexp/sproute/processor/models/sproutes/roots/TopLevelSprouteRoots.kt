package com.casadetasha.kexp.sproute.processor.models.sproutes.roots

import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteAuthentication
import com.squareup.kotlinpoet.TypeName

internal sealed class TopLevelSprouteRoot() : SprouteRoot {
    override fun failIfChildRootIsCyclical(childRootKey: TypeName) {
        // Since there are no parents there is nothing more to check
    }
}

internal class AnnotatedSprouteRoot(
    override val childRootKey: TypeName,
    override val sprouteAuthentication: SprouteAuthentication,
    val packageName: String,
    val routeSegment: String,
    val canAppendPackage: Boolean
) : TopLevelSprouteRoot() {

    override fun getSproutePathForPackage(sproutePackage: String): String {
        return if (canAppendPackage) {
            routeSegment + sproutePackage.asSubPackageOf(packageName).asPath().lowercase()
        } else routeSegment
    }
}

internal class DefaultSprouteSprouteRoot(
    override val childRootKey: TypeName
) : TopLevelSprouteRoot() {

    override val sprouteAuthentication: SprouteAuthentication = SprouteAuthentication.BaseAuthentication()
    override fun getSproutePathForPackage(sproutePackage: String) = ""
}
