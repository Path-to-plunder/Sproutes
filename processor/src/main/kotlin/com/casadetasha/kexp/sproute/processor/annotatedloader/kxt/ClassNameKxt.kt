package com.casadetasha.kexp.sproute.processor.annotatedloader.kxt

import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData
import com.squareup.kotlinpoet.metadata.specs.containerData

@OptIn(KotlinPoetMetadataPreview::class)
internal fun ClassName.getClassData(): ClassData {
    val classInspector = ElementsClassInspector.create(SprouteAnnotationProcessor.processingEnvironment.elementUtils, SprouteAnnotationProcessor.processingEnvironment.typeUtils)
    val containerData = classInspector.containerData(this, null)
    check(containerData is ClassData) { "Unexpected container data type: ${containerData.javaClass}" }
    return containerData
}
