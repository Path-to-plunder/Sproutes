package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.MemberNames.convertToMemberNames
import com.casadetasha.kexp.sproute.processor.ktx.*
import com.casadetasha.kexp.sproute.processor.ktx.asMethod
import com.casadetasha.kexp.sproute.processor.ktx.primaryConstructor
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.ImmutableKmPackage
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

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
        private val classRouteSegment: String,
        private val rootPathSegment: String,
        private val requestMethodMap: Map<String, ExecutableElement>
    ) : SprouteKotlinParent(
        packageName = classData.className.packageName,
        classSimpleName = classData.className.simpleName
    ) {

        val primaryConstructorParams: List<MemberName>? by lazy {
            classData.primaryConstructor()
                ?.valueParameters
                ?.convertToMemberNames()
        }

        override val requestFunctions: Set<RequestFunction> by lazy {
            classData.methods
                .filter { requestMethodMap.containsKey(it.key.name) }
                .map { entry ->
                    RequestFunction(
                        packageName = packageName,
                        methodElement = requestMethodMap[entry.key.name]!!,
                        function = entry.key,
                        pathRootSegment = rootPathSegment,
                        classRouteSegment = classRouteSegment,
                        defaultAuthenticationStatus = Unauthenticated::class
                    )
                }
                .toSortedSet()
        }
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    internal class SproutePackage(
        immutableKmPackage: ImmutableKmPackage,
        packageName: String,
        fileName: String,
        requestMethodMap: Map<String, Element>
    ) : SprouteKotlinParent(
        packageName = packageName,
        classSimpleName = fileName
    ) {

        override val requestFunctions: Set<RequestFunction> by lazy {
            immutableKmPackage.functions
                .filter { requestMethodMap.containsKey(it.name) }
                .map {
                    val methodElement = requestMethodMap[it.name]!!
                    RequestFunction(
                        packageName = packageName,
                        methodElement = methodElement,
                        function = it,
                        pathRootSegment = methodElement.getTopLevelFunctionPathRoot(),
                        classRouteSegment = "",
                        Unauthenticated::class
                    )
                }.toSortedSet()
        }
    }
}
