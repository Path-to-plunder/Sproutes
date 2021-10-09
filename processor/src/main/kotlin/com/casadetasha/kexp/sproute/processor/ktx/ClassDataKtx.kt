package com.casadetasha.kexp.sproute.processor.ktx

import com.squareup.kotlinpoet.metadata.ImmutableKmConstructor
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isPrimary
import com.squareup.kotlinpoet.metadata.specs.ClassData

@OptIn(KotlinPoetMetadataPreview::class)
internal fun ClassData.primaryConstructor(): ImmutableKmConstructor? {
    return constructors.keys.firstOrNull { it.isPrimary }
}
