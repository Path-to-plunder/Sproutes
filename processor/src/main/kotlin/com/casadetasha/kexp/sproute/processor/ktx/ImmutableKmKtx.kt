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

@OptIn(KotlinPoetMetadataPreview::class)
internal fun ImmutableKmType.toMemberName(): MemberName {
    val name: ClassName = (classifier as KmClassifier.Class).name
    return MemberName(name.packageName, name.simpleName)
}

private val ClassName.packageName: String
    get() {
        val segments = this.split('/').toMutableList()
        segments.removeLast()
        return segments.joinToString(".")
    }

private val ClassName.simpleName: String
    get() = this.split('/').last()
