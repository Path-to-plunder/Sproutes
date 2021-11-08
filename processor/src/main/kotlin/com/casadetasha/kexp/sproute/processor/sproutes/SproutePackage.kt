package com.casadetasha.kexp.sproute.processor.sproutes

import com.casadetasha.kexp.annotationparser.KotlinValue
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.ktx.getSprouteRootKey
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.AuthLazyLoader
import com.casadetasha.kexp.sproute.processor.sproutes.segments.ProcessedRouteSegments
import com.casadetasha.kexp.sproute.processor.sproutes.segments.ProcessedRouteSegments.defaultSegmentKey
import com.casadetasha.kexp.sproute.processor.sproutes.segments.TrailingRouteSegment
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview

@OptIn(KotlinPoetMetadataPreview::class)
internal class SproutePackage(
    packageName: String,
    fileName: String,
    functions: Set<KotlinValue.KotlinFunction>
) : SprouteParent(
    packageName = packageName,
    classSimpleName = fileName
) {

    override val sprouteRequestFunctions: Set<SprouteRequestFunction> = functions
        .map {
            val sprouteAnnotation: Sproute? = it.element.getAnnotation(Sproute::class.java)
            val sprouteRootKey = sprouteAnnotation?.getSprouteRootKey() ?: defaultSegmentKey

            val segment = TrailingRouteSegment(
                routeSegment = sprouteAnnotation?.routeSegment ?: "",
                parentSegmentKey = sprouteRootKey,
                segmentKey = ClassName(it.packageName, "kexp_sproute\$_${it.function.name}"),
                authLazyLoader = AuthLazyLoader(sprouteRootKey, it.element)
            ).apply { ProcessedRouteSegments.put(this) }

            SprouteRequestFunction(
                sprouteRootKey = segment.segmentKey,
                kotlinFunction = it
            )
        }.toSet()

}
