package com.casadetasha.kexp.sproute.processor.sproutes

import com.casadetasha.kexp.annotationparser.KotlinValue
import com.casadetasha.kexp.sproute.processor.sproutes.segments.RouteSegment
import com.casadetasha.kexp.sproute.processor.values.SprouteParameter
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData

@OptIn(KotlinPoetMetadataPreview::class)
internal open class SprouteClass(
    private val routeSegment: RouteSegment,
    val classData: ClassData,
    val primaryConstructorParams: List<SprouteParameter>?,
    functions: Set<KotlinValue.KotlinFunction>
) : SprouteParent(
    packageName = classData.className.packageName,
    classSimpleName = classData.className.simpleName
) {

    override val sprouteRequestFunctions: Set<SprouteRequestFunction> by lazy {
        functions
            .map {
                SprouteRequestFunction(
                    sprouteRootKey = routeSegment.segmentKey,
                    kotlinFunction = it
                )
            }.toSet()
    }

}
