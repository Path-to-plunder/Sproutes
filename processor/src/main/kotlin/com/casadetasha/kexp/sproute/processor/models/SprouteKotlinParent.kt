package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.annotationparser.KotlinFunction
import com.casadetasha.kexp.sproute.processor.ktx.asMethod
import com.casadetasha.kexp.sproute.processor.ktx.getTopLevelFunctionPathRoot
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData

internal sealed class SprouteKotlinParent(
    val packageName: String,
    val classSimpleName: String
) : Comparable<SprouteKotlinParent> {

    val memberName = MemberName(packageName, classSimpleName)
    val fileName = "InstaRoutes\$${packageName}_${classSimpleName}"
    val configurationMethodSimpleName = "configureRoutes\$${packageName}_$classSimpleName".asMethod()
    val configurationMethodName: MemberName = MemberName(packageName, configurationMethodSimpleName)

    abstract val sprouteRequestFunctions: Set<SprouteRequestFunction>

    override fun compareTo(other: SprouteKotlinParent): Int {
        return configurationMethodSimpleName.compareTo(other.configurationMethodSimpleName)
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    internal class SprouteClass(
        val classData: ClassData,
        val primaryConstructorParams: List<MemberName>?,
        classRouteSegment: String,
        rootPathSegment: String,
        functions: Set<KotlinFunction>,
        authentication: Authentication
    ) : SprouteKotlinParent(
        packageName = classData.className.packageName,
        classSimpleName = classData.className.simpleName
    ) {

        override val sprouteRequestFunctions: Set<SprouteRequestFunction> = functions
            .map {
                SprouteRequestFunction(
                    kotlinFunction = it,
                    pathRootSegment = rootPathSegment,
                    classRouteSegment = classRouteSegment,
                    authentication = authentication.forChildElement(it.element)
                )
            }.toSortedSet()
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    internal class SproutePackage(
        packageName: String,
        fileName: String,
        functions: Set<KotlinFunction>
    ) : SprouteKotlinParent(
        packageName = packageName,
        classSimpleName = fileName
    ) {

        override val sprouteRequestFunctions: Set<SprouteRequestFunction> = functions
            .map {
                SprouteRequestFunction(
                    kotlinFunction = it,
                    pathRootSegment = it.element.getTopLevelFunctionPathRoot(),
                    classRouteSegment = "",
                    authentication = Authentication(
                        element = it.element,
                        parentAuthenticatedAnnotation = null
                    )
                )
            }.toSortedSet()
    }
}
