package com.casadetasha.kexp.sproute.processor.sproutes.segments

import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.Authentication
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.BaseAuthentication
import com.squareup.kotlinpoet.TypeName

internal sealed class LeadingRouteSegment : RouteSegment {

    internal class AnnotatedRouteSegment(
        override val segmentKey: TypeName,
        override val authentication: Authentication,
        val packageName: String,
        val routeSegment: String
    ) : LeadingRouteSegment() {

        override fun getSproutePathForPackage(sproutePackage: String): String {
            return routeSegment + sproutePackage.asSubPackageOf(packageName).asPath().lowercase()
        }
    }

    internal class DefaultRouteSegment(
        override val segmentKey: TypeName
    ) : LeadingRouteSegment() {

        override val authentication: Authentication = BaseAuthentication()
        override fun getSproutePathForPackage(sproutePackage: String) = ""
    }

    override fun failIfChildSegmentIsCyclical(childSegmentKeys: List<TypeName>) {
        // Since there are no parents there is nothing more to check
    }
}
