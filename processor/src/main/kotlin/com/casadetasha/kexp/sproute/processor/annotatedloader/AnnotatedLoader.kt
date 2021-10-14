package com.casadetasha.kexp.sproute.processor.annotatedloader

import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.ktx.*
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.*
import com.squareup.kotlinpoet.metadata.specs.ClassData
import com.squareup.kotlinpoet.metadata.specs.containerData
import com.squareup.kotlinpoet.metadata.specs.internal.ClassInspectorUtil
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
            val key = it.enclosingElement.asKey()
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

@OptIn(KotlinPoetMetadataPreview::class)
fun RoundEnvironment.getClassesAnnotatedWith(
    annotationClass: KClass<out Annotation>
): ImmutableSet<KotlinContainer.KotlinClass> = getElementsAnnotatedWith(annotationClass.java)
    .filterNot { it.isTopLevelFunction() }
    .mapToImmutableSet {
        val className = it.getClassName()
        KotlinContainer.KotlinClass(
            element = it,
            className = className,
            classData = className.getClassData(),
            functionMap = it.getRequestMethods()
        )
    }

@OptIn(KotlinPoetMetadataPreview::class)
private fun Element.getParentFileKmPackage(): ImmutableKmPackage =
    enclosingElement.getAnnotation(Metadata::class.java)!!
        .toKotlinClassMetadata<KotlinClassMetadata.FileFacade>()
        .toImmutableKmPackage()

@OptIn(KotlinPoetMetadataPreview::class)
private fun Element.isTopLevelFunction() =
    enclosingElement.getAnnotation(Metadata::class.java)
        ?.readKotlinClassMetadata()
        ?.header
        ?.kind == KotlinClassHeader.FILE_FACADE_KIND

@OptIn(KotlinPoetMetadataPreview::class)
internal fun Element.getClassName(): ClassName {
    val typeMetadata = getAnnotation(Metadata::class.java)
    val kmClass = typeMetadata.toImmutableKmClass()
    return ClassInspectorUtil.createClassName(kmClass.name)
}

@OptIn(KotlinPoetMetadataPreview::class)
private fun ClassName.getClassData(): ClassData {
    val classInspector = ElementsClassInspector.create(SprouteAnnotationProcessor.processingEnvironment.elementUtils, SprouteAnnotationProcessor.processingEnvironment.typeUtils)
    val containerData = classInspector.containerData(this, null)
    check(containerData is ClassData) { "Unexpected container data type: ${containerData.javaClass}" }
    return containerData
}

private fun Element.asKey(): String {
    val packageName = SprouteAnnotationProcessor.processingEnvironment.elementUtils.getPackageOf(this).qualifiedName.toString()
    val containerName = enclosingElement.simpleName.toString()
    return "${packageName}.${containerName}.${simpleName}"
}
