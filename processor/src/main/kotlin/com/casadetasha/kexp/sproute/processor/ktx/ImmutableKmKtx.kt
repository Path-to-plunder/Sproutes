package com.casadetasha.kexp.sproute.processor.ktx

import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import kotlinx.metadata.ClassName
import kotlinx.metadata.KmClassifier

@OptIn(KotlinPoetMetadataPreview::class)
internal fun ImmutableKmValueParameter.asCanonicalName(): String {
    val clazz = type!!.classifier as KmClassifier.Class
    return clazz.name.replace("/", ".")
}
