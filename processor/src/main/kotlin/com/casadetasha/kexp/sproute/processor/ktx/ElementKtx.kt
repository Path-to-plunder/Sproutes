package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.processor.RequestAnnotations
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.*
import com.squareup.kotlinpoet.metadata.specs.ClassData
import com.squareup.kotlinpoet.metadata.specs.containerData
import com.squareup.kotlinpoet.metadata.specs.internal.ClassInspectorUtil
import kotlinx.metadata.jvm.KotlinClassHeader.Companion.FILE_FACADE_KIND
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import kotlin.reflect.KClass

internal fun Element.hasAnnotation(clazz: Class<out Annotation>): Boolean {
    return getAnnotation(clazz) != null
}

@OptIn(KotlinPoetMetadataPreview::class)
internal fun Element.getClassData(processingEnv: ProcessingEnvironment): ClassData {
    val classInspector = ElementsClassInspector.create(processingEnv.elementUtils, processingEnv.typeUtils)
    val containerData = classInspector.containerData(this.getClassName(), null)
    check(containerData is ClassData) { "Unexpected container data type: ${containerData.javaClass}" }
    return containerData
}

@OptIn(KotlinPoetMetadataPreview::class)
internal fun Element.getClassName(): ClassName {
    val typeMetadata = getAnnotation(Metadata::class.java)
    val kmClass = typeMetadata.toImmutableKmClass()
    return ClassInspectorUtil.createClassName(kmClass.name)
}

internal fun Element.getRequestMethods(): Map<String, ExecutableElement> {
    val requestMap: HashMap<String, ExecutableElement> = HashMap()
    RequestAnnotations.validRequestTypes.forEach {
        requestMap.putAll(getMethodsForRequestType(it))
    }
    return requestMap
}

internal fun Element.getMethodsForRequestType(requestTypeClass: KClass<out Annotation>):
        Map<String, ExecutableElement> {
    val methodMap: HashMap<String, ExecutableElement> = HashMap()

    enclosedElements.forEach {
        if (it.kind == ElementKind.METHOD && it.hasAnnotation(requestTypeClass.java)) {
            methodMap += it.simpleName.toString() to it as ExecutableElement
        }
    }

    return methodMap
}

@OptIn(KotlinPoetMetadataPreview::class)
internal fun Element.getParentFileKmPackage(): ImmutableKmPackage =
    enclosingElement.getAnnotation(Metadata::class.java)!!
        .toKotlinClassMetadata<KotlinClassMetadata.FileFacade>()
        .toImmutableKmPackage()

@OptIn(KotlinPoetMetadataPreview::class)
internal fun Element.isOrphanFunction() =
    enclosingElement.getAnnotation(Metadata::class.java)
        ?.readKotlinClassMetadata()
        ?.header
        ?.kind == FILE_FACADE_KIND

internal fun Element.getQualifiedName(processingEnv: ProcessingEnvironment): String {
    val packageName = processingEnv.elementUtils.getPackageOf(this).qualifiedName.toString()
    val containerName = enclosingElement.simpleName.toString()
    return "${packageName}.${containerName}.${simpleName}"
}
