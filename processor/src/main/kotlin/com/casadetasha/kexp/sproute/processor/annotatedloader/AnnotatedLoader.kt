package com.casadetasha.kexp.sproute.processor.annotatedloader

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.ktx.*
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.*
import com.squareup.kotlinpoet.metadata.specs.ClassData
import com.squareup.kotlinpoet.metadata.specs.containerData
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import kotlin.reflect.KClass

@OptIn(KotlinPoetMetadataPreview::class)
fun RoundEnvironment.getFileFacadesForTopLevelFunctionsAnnotatedWith(
    annotations: List<KClass<out Annotation>>
): ImmutableSet<KotlinContainer.KotlinFileFacade> {
    val functionListMap = HashMap<String, MutableList<Element>>()
    val functionFileElementMap = HashMap<String, Element>()
    getElementsAnnotatedWithAny(annotations.map { it.java }.toSet())
        .filter { it.isTopLevelFunction() }
        .forEach {
            val key = it.enclosingElement.getQualifiedName()
            functionListMap.getOrCreateList(key).add(it)
            functionFileElementMap[key] = functionFileElementMap[key] ?: it.enclosingElement
        }

    return functionListMap.map {
        val fileElement = functionFileElementMap[it.key]!!
        KotlinContainer.KotlinFileFacade(
            element = fileElement,
            immutableKmPackage = it.value.first().getParentFileKmPackage(),
            packageName = fileElement.packageName,
            fileName = fileElement.simpleName?.toString() ?: "",
            functionMap = it.value.toMap()
        )
    }.toImmutableSet()
}

internal fun RoundEnvironment.getClassesAnnotatedWith(
    annotation: Annotation
): ImmutableSet<SprouteKotlinParent.SprouteClass> = getElementsAnnotatedWith(annotation.javaClass)
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

@OptIn(KotlinPoetMetadataPreview::class)
fun Element.getParentFileKmPackage(): ImmutableKmPackage =
    enclosingElement.getAnnotation(Metadata::class.java)!!
        .toKotlinClassMetadata<KotlinClassMetadata.FileFacade>()
        .toImmutableKmPackage()

@OptIn(KotlinPoetMetadataPreview::class)
fun Element.isTopLevelFunction() =
    enclosingElement.getAnnotation(Metadata::class.java)
        ?.readKotlinClassMetadata()
        ?.header
        ?.kind == KotlinClassHeader.FILE_FACADE_KIND

@OptIn(KotlinPoetMetadataPreview::class)
fun Element.getClassData(): ClassData {
    val classInspector = ElementsClassInspector.create(SprouteAnnotationProcessor.processingEnvironment.elementUtils, SprouteAnnotationProcessor.processingEnvironment.typeUtils)
    val containerData = classInspector.containerData(this.getClassName(), null)
    check(containerData is ClassData) { "Unexpected container data type: ${containerData.javaClass}" }
    return containerData
}
