package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations
import com.casadetasha.kexp.sproute.processor.annotatedloader.getFileFacadesForTopLevelFunctionsAnnotatedWith
import com.casadetasha.kexp.sproute.processor.annotatedloader.isTopLevelFunction
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRootInfo
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

internal fun RoundEnvironment.getSprouteRoots() :
        ImmutableMap<TypeName, SprouteRootInfo> = with(HashMap<TypeName, SprouteRootInfo>()) {
    SprouteRoot::class.asTypeName().let {
        this[it] = SprouteRootInfo(it.packageName, "", canAppendPackage = false)
    }

    getElementsAnnotatedWith(SprouteRoot::class.java)?.forEach { classElement ->
        classElement.getAnnotation(SprouteRoot::class.java).let {
            val className = classElement.getClassName()
            this[className] = SprouteRootInfo(className.packageName, it.rootSprouteSegment, it.appendSubPackagesAsSegments)
        }
    }

    ImmutableMap.copyOf(this)
}
@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.getRoutePackages(): ImmutableSet<SprouteKotlinParent.SproutePackage> {
    return getFileFacadesForTopLevelFunctionsAnnotatedWith(SprouteRequestAnnotations.validRequestTypes)
        .map { SprouteKotlinParent.SproutePackage(
            immutableKmPackage = it.immutableKmPackage,
            packageName = it.packageName,
            fileName = it.fileName,
            requestMethodMap = it.functionMap
        ) }
        .toImmutableSet()
}

internal fun RoundEnvironment.getRouteClasses(): ImmutableSet<SprouteKotlinParent.SprouteClass> =
    getElementsAnnotatedWith(Sproute::class.java)
        .filterNot { it.isTopLevelFunction() }
        .mapToImmutableSet { createRouteClass(it) }

@OptIn(KotlinPoetMetadataPreview::class)
private fun createRouteClass(routeClassElement: Element): SprouteKotlinParent.SprouteClass {
    val classSprouteAnnotation: Sproute = routeClassElement.getAnnotation(Sproute::class.java)
    val routeRoot = classSprouteAnnotation.getSprouteRoot()
    val classData = routeClassElement.getClassData()

    return SprouteKotlinParent.SprouteClass(
        classData = classData,
        rootPathSegment = routeRoot.getPathPrefixToSproutePackage(classData.className.packageName),
        classRouteSegment = classSprouteAnnotation.routeSegment,
        requestMethodMap = routeClassElement.getRequestMethods()
    )
}
