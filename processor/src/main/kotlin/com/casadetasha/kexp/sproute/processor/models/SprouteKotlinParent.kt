package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.MemberNames.convertToMemberNames
import com.casadetasha.kexp.sproute.processor.annotatedloader.KotlinFunction
import com.casadetasha.kexp.sproute.processor.ktx.asMethod
import com.casadetasha.kexp.sproute.processor.ktx.getTopLevelFunctionPathRoot
import com.casadetasha.kexp.sproute.processor.ktx.primaryConstructor
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

    abstract val requestFunctions: Set<RequestFunction>

    override fun compareTo(other: SprouteKotlinParent): Int {
        return configurationMethodSimpleName.compareTo(other.configurationMethodSimpleName)
    }


    @OptIn(KotlinPoetMetadataPreview::class)
    internal class SprouteClass(
        val classData: ClassData,
        classRouteSegment: String,
        rootPathSegment: String,
        functions: Set<KotlinFunction>
    ) : SprouteKotlinParent(
        packageName = classData.className.packageName,
        classSimpleName = classData.className.simpleName
    ) {

        val primaryConstructorParams: List<MemberName>? by lazy {
            classData.primaryConstructor()
                ?.valueParameters
                ?.convertToMemberNames()
        }

        override val requestFunctions: Set<RequestFunction> = functions
            .map {
                RequestFunction(
                    kotlinFunction = it,
                    pathRootSegment = rootPathSegment,
                    classRouteSegment = classRouteSegment,
                    defaultAuthStatus = Unauthenticated::class
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

        override val requestFunctions: Set<RequestFunction> = functions
            .map {
                RequestFunction(
                    kotlinFunction = it,
                    pathRootSegment = it.element.getTopLevelFunctionPathRoot(),
                    classRouteSegment = "",
                    Unauthenticated::class
                )
            }.toSortedSet()
    }
}
