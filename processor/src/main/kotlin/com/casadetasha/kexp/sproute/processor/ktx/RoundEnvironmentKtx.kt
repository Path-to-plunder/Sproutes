package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations
import com.casadetasha.kexp.sproute.processor.annotatedloader.getClassesAnnotatedWith
import com.casadetasha.kexp.sproute.processor.annotatedloader.getFileFacadesForTopLevelFunctionsAnnotatedWith
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRootInfo
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment

internal fun RoundEnvironment.getSprouteRoots() :
        ImmutableMap<TypeName, SprouteRootInfo> = with(HashMap<TypeName, SprouteRootInfo>()) {
    SprouteRoot::class.asTypeName().let {
        this[it] = SprouteRootInfo(it.packageName, "", canAppendPackage = false)
    }

    getClassesAnnotatedWith(SprouteRoot::class).forEach {
        val annotation = it.element.getAnnotation(SprouteRoot::class.java)!!
        this[it.className] = SprouteRootInfo(
            it.className.packageName,
            annotation.rootSprouteSegment,
            annotation.appendSubPackagesAsSegments)
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

@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.getRouteClasses(): ImmutableSet<SprouteKotlinParent.SprouteClass> =
    getClassesAnnotatedWith(Sproute::class)
        .map {
            val classSprouteAnnotation: Sproute = it.element.getAnnotation(Sproute::class.java)
            val routeRoot = classSprouteAnnotation.getSprouteRoot()

            SprouteKotlinParent.SprouteClass(
                classData = it.classData,
                rootPathSegment = routeRoot.getPathPrefixToSproutePackage(it.packageName),
                classRouteSegment = classSprouteAnnotation.routeSegment,
                requestMethodMap = it.functionMap.toMap()
            )
        }
        .toImmutableSet()
