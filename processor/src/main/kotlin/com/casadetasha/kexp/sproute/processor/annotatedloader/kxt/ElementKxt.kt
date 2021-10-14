package com.casadetasha.kexp.sproute.processor.annotatedloader.kxt

import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.*
import com.squareup.kotlinpoet.metadata.specs.internal.ClassInspectorUtil
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.Element

@OptIn(KotlinPoetMetadataPreview::class)
internal fun Element.getParentFileKmPackage(): ImmutableKmPackage =
    enclosingElement.getAnnotation(Metadata::class.java)!!
        .toKotlinClassMetadata<KotlinClassMetadata.FileFacade>()
        .toImmutableKmPackage()

@OptIn(KotlinPoetMetadataPreview::class)
internal fun Element.isTopLevelFunction() =
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

internal fun Element.asKey(): String {
    val packageName = SprouteAnnotationProcessor.processingEnvironment.elementUtils.getPackageOf(this).qualifiedName.toString()
    val containerName = enclosingElement.simpleName.toString()
    return "${packageName}.${containerName}.${simpleName}"
}
