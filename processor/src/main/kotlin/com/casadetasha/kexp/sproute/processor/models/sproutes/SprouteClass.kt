package com.casadetasha.kexp.sproute.processor.models.sproutes

import com.casadetasha.kexp.annotationparser.KotlinValue
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.SprouteSegment
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData

@OptIn(KotlinPoetMetadataPreview::class)
internal open class SprouteClass(
    private val sprouteSegment: SprouteSegment,
    val classData: ClassData,
    val primaryConstructorParams: List<MemberName>?,
    functions: Set<KotlinValue.KotlinFunction>
) : SprouteParent(
    packageName = classData.className.packageName,
    classSimpleName = classData.className.simpleName
) {

    override val sprouteRequestFunctions: Set<SprouteRequestFunction> by lazy {
        functions
            .map {
                SprouteRequestFunction(
                    sprouteRootKey = sprouteSegment.segmentKey,
                    kotlinFunction = it
                )
            }.toSet()
    }

}
