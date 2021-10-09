package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.MemberNames.convertToMemberNames
import com.casadetasha.kexp.sproute.processor.ktx.asMethod
import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf
import com.casadetasha.kexp.sproute.processor.ktx.getRequestMethods
import com.casadetasha.kexp.sproute.processor.ktx.primaryConstructor
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.ImmutableKmPackage
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData
import javax.annotation.processing.ProcessingEnvironment
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

    abstract val requestFunction: Set<RequestFunction>

    fun getMethodFunctionName(requestFunction: RequestFunction): MemberName {
        return MemberName(packageName, requestFunction.functionSimpleName)
    }

    override fun compareTo(other: SprouteKotlinParent): Int {
        return configurationMethodSimpleName.compareTo(other.configurationMethodSimpleName)
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    internal class SprouteClass(
        private val processingEnv: ProcessingEnvironment,
        val classData: ClassData,
        private val classRouteSegment: String,
        sprouteRootInfo: SprouteRootInfo,
        classElement: Element
    ) : SprouteKotlinParent(
        packageName = classData.className.packageName,
        classSimpleName = classData.className.simpleName
    ) {
        private val requestMethodMap: Map<String, ExecutableElement> = classElement.getRequestMethods()
        private val rootPathSegment = if (sprouteRootInfo.canAppendPackage) {
            sprouteRootInfo.routeSegment + packageName.asSubPackageOf(sprouteRootInfo.packageName).asPath().lowercase()
        } else sprouteRootInfo.routeSegment

        val primaryConstructorParams: List<MemberName>? = classData.primaryConstructor()
            ?.valueParameters
            ?.convertToMemberNames()

        override val requestFunction: Set<RequestFunction> = classData.methods
            .filter { requestMethodMap.containsKey(it.key.name) }
            .map { entry ->
                RequestFunction(
                    methodElement = requestMethodMap[entry.key.name]!!,
                    processingEnv = processingEnv,
                    function = entry.key,
                    pathRootSegment = rootPathSegment,
                    classRouteSegment = classRouteSegment,
                    defaultAuthenticationStatus = Unauthenticated::class
                )
            }
            .toSortedSet()
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    internal class SproutePackage(
        processingEnv: ProcessingEnvironment,
        immutableKmPackage: ImmutableKmPackage,
        packageElement: Element,
        fileName: String,
        methodElementSet: Map<String, Element>
    ) : SprouteKotlinParent(
        packageName = processingEnv.elementUtils.getPackageOf(packageElement).qualifiedName.toString(),
        classSimpleName = fileName
    ) {
        override val requestFunction: Set<RequestFunction> = immutableKmPackage.functions
            .filter { methodElementSet.containsKey(it.name) }
            .map {
                RequestFunction(
                    processingEnv = processingEnv,
                    methodElement = methodElementSet[it.name]!!,
                    function = it,
                    pathRootSegment = "",
                    classRouteSegment = "",
                    Unauthenticated::class
                )
            }.toSortedSet()
    }
}

