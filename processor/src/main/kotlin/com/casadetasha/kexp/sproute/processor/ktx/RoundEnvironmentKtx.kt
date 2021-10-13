package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations
import com.casadetasha.kexp.sproute.processor.annotatedloader.getParentFileKmPackage
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
    val functionListMap = HashMap<String, MutableList<Element>>()
    val functionFileElementMap = HashMap<String, Element>()
    getElementsAnnotatedWithAny(SprouteRequestAnnotations.validRequestTypes.map { it.java }.toSet())
        .filter { it.isTopLevelFunction() }
        .forEach {
            val key = it.enclosingElement.getQualifiedName()
            functionListMap.getOrCreateList(key).add(it)
            functionFileElementMap[key] = functionFileElementMap[key] ?: it.enclosingElement
        }

    return functionListMap.map {
        val fileElement = functionFileElementMap[it.key]!!
        SprouteKotlinParent.SproutePackage(
            immutableKmPackage = it.value.first().getParentFileKmPackage(),
            packageName = fileElement.packageName,
            fileName = fileElement.simpleName?.toString() ?: "",
            requestMethodMap = it.value.toMap()
        )
    }.toImmutableSet()
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
