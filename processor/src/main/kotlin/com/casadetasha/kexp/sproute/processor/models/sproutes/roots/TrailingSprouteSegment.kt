package com.casadetasha.kexp.sproute.processor.models.sproutes.roots

import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.AuthLazyLoader
import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.Authentication
import com.squareup.kotlinpoet.TypeName

internal class TrailingSprouteSegment(
    override val segmentKey: TypeName,
    val parentRootKey: TypeName,
    val routeSegment: String,
    authLazyLoader: AuthLazyLoader
): SprouteSegment {

    private val parentSegment: SprouteSegment by lazy {
        ProcessedSprouteSegments.getSprouteRootForChild(
            parentRootKey,
            segmentKey
        )
    }

    private val parentRootKeys: Set<TypeName> by lazy {
        if (parentSegment is TrailingSprouteSegment) {
            (parentSegment as TrailingSprouteSegment).parentRootKeys
        } else {
            setOf(parentSegment.segmentKey)
        }
    }

    override val authentication: Authentication by lazy { authLazyLoader.value }

    override fun getSproutePathForPackage(sproutePackage: String): String {
        val parentSegments = parentSegment.getSproutePathForPackage(sproutePackage)
        return "${parentSegments}$routeSegment"
    }

    override fun failIfChildRootIsCyclical(childRootKey: TypeName) {
        val allRoots = parentRootKeys + this.segmentKey
        if (allRoots.contains(childRootKey)) {
            SprouteAnnotationProcessor.processingEnvironment.printThenThrowError(
                "Found cyclical Root dependency adding $childRootKey to root hierarchy" +
                        " ( ${allRoots.joinToString(", ")} )")
        }
    }
}