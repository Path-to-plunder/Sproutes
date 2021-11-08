package com.casadetasha.kexp.sproute.processor.models.sproutes

import com.casadetasha.kexp.annotationparser.KotlinValue
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.ktx.getSprouteRootKey
import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.AuthLazyLoader
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.ProcessedSprouteSegments
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.ProcessedSprouteSegments.defaultSegmentKey
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.TrailingSprouteSegment
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

            val segment = TrailingSprouteSegment(
                routeSegment = sprouteAnnotation?.routeSegment ?: "",
                parentRootKey = sprouteRootKey,
                segmentKey = ClassName(it.packageName, "kexp_sproute\$_${it.function.name}"),
                authLazyLoader = AuthLazyLoader(sprouteRootKey, it.element)
            ).apply { ProcessedSprouteSegments.put(this) }

            SprouteRequestFunction(
                sprouteRootKey = segment.segmentKey,
                kotlinFunction = it
            )
        }.toSet()

}
