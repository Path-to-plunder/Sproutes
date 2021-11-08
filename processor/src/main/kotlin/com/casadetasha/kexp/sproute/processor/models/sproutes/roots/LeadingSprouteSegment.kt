package com.casadetasha.kexp.sproute.processor.models.sproutes.roots

import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf
import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.Authentication
import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.BaseAuthentication
import com.squareup.kotlinpoet.TypeName

internal sealed class TopLevelSprouteSegment() :
    SprouteSegment {
    override fun failIfChildRootIsCyclical(childRootKey: TypeName) {
        // Since there are no parents there is nothing more to check
    }
}

internal class AnnotatedSprouteSegment(
    override val segmentKey: TypeName,
    override val authentication: Authentication,
    val packageName: String,
    val routeSegment: String,
    val canAppendPackage: Boolean
) : TopLevelSprouteSegment() {

    override fun getSproutePathForPackage(sproutePackage: String): String {
        return if (canAppendPackage) {
            routeSegment + sproutePackage.asSubPackageOf(packageName).asPath().lowercase()
        } else routeSegment
    }
}

internal class DefaultSprouteSprouteSegment(
    override val segmentKey: TypeName
) : TopLevelSprouteSegment() {

    override val authentication: Authentication = BaseAuthentication()
    override fun getSproutePathForPackage(sproutePackage: String) = ""
}
