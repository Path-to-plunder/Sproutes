package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.*
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import kotlinx.metadata.KmClassifier
import javax.lang.model.element.Element
import kotlin.reflect.KClass

internal fun Element.getTopLevelFunctionPathRoot(): String {
    val sprouteAnnotation = getAnnotation(Sproute::class.java)
    val sprouteRootSegment = sprouteAnnotation?.getSprouteRoot()?.getPathPrefixToSproutePackage(packageName) ?: ""
    val sprouteSegment = sprouteAnnotation?.routeSegment ?: ""

    return sprouteRootSegment + sprouteSegment
}

internal val Element.packageName: String
    get() {
        val packageElement = SprouteAnnotationProcessor.processingEnvironment.elementUtils.getPackageOf(this)
        return SprouteAnnotationProcessor.processingEnvironment.elementUtils.getPackageOf(packageElement).qualifiedName.toString()
    }

@OptIn(KotlinPoetMetadataPreview::class)
internal fun ImmutableKmValueParameter.asCanonicalName(): String {
    val clazz = type!!.classifier as KmClassifier.Class
    return clazz.name.replace("/", ".")
}

internal fun KClass<*>.asCanonicalName(): String = asTypeName().canonicalName

internal fun KClass<*>.toMemberName(): MemberName {
    return MemberName(asClassName().packageName, asClassName().simpleName)
}

internal fun Annotation.asKClass(): KClass<out Annotation> {
    return when(this){
        is Get -> Get::class
        is Post -> Post::class
        is Put -> Put::class
        is Patch -> Patch::class
        is Delete -> Delete::class
        is Head -> Head::class
        is Options -> Options::class
        else -> throw IllegalArgumentException("Provided annotation must be one of the types in validRequestList")
    }
}
