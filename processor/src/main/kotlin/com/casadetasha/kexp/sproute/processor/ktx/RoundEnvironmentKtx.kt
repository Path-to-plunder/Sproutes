package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.validRequestTypes
import com.casadetasha.kexp.sproute.processor.annotatedloader.kxt.getClassesAnnotatedWith
import com.casadetasha.kexp.sproute.processor.annotatedloader.kxt.getFileFacadesForTopLevelFunctionsAnnotatedWith
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRootInfo
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment

internal fun RoundEnvironment.getSprouteRoots(): Map<TypeName, SprouteRootInfo> = HashMap<TypeName, SprouteRootInfo>()
    .apply {
        SprouteRoot::class.asTypeName().let {
            this[it] = SprouteRootInfo(it.packageName, "", canAppendPackage = false)
        }

        getClassesAnnotatedWith(SprouteRoot::class).forEach {
            val annotation = it.element.getAnnotation(SprouteRoot::class.java)!!
            this[it.className] = SprouteRootInfo(
                it.className.packageName,
                annotation.rootSprouteSegment,
                annotation.appendSubPackagesAsSegments
            )
        }
    }.toMap()

@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.getRoutePackages(): Set<SprouteKotlinParent.SproutePackage> {
    return getFileFacadesForTopLevelFunctionsAnnotatedWith(SprouteRequestAnnotations.validRequestTypes)
        .map {
            SprouteKotlinParent.SproutePackage(
                immutableKmPackage = it.immutableKmPackage,
                packageName = it.packageName,
                fileName = it.fileName,
                requestMethodMap = it.functionMap
            )
        }
        .toSet()
}

@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.getRouteClasses(): Set<SprouteKotlinParent.SprouteClass> =
    getClassesAnnotatedWith(Sproute::class)
        .map {
            val classSprouteAnnotation: Sproute = it.element.getAnnotation(Sproute::class.java)
            val routeRoot = classSprouteAnnotation.getSprouteRoot()

            SprouteKotlinParent.SprouteClass(
                classData = it.classData,
                rootPathSegment = routeRoot.getPathPrefixToSproutePackage(it.packageName),
                classRouteSegment = classSprouteAnnotation.routeSegment,
                requestMethodMap = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray())
            )
        }
        .toSet()
