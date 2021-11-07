package com.casadetasha.kexp.sproute.processor.models.sproutes

import com.casadetasha.kexp.annotationparser.KotlinValue
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.ktx.getSprouteRootKey
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
            val classSprouteAnnotation: Sproute? = it.element.getAnnotation(Sproute::class.java)

            SprouteRequestFunction(
                sprouteRootKey = classSprouteAnnotation?.getSprouteRootKey(),
                kotlinFunction = it,
                classRouteSegment = classSprouteAnnotation?.routeSegment ?: ""
            )
        }.toSet()
}
