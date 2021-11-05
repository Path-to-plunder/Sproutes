package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.*
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.models.Root
import com.casadetasha.kexp.sproute.processor.models.Root.Companion.sprouteRoots
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import kotlinx.metadata.KmClassifier
import javax.lang.model.type.MirroredTypeException
import kotlin.reflect.KClass

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

internal fun Sproute.getSprouteRoot(): Root {
    val root = sprouteRoots[getRootTypeName().toString()]
    return root ?: SprouteAnnotationProcessor.processingEnvironment.printThenThrowError(
        "@SprouteRoot annotation was not found for provided class $root"
    )
}

// asTypeName() should be safe since custom routes will never be Kotlin core classes
@OptIn(DelicateKotlinPoetApi::class)
private fun Sproute.getRootTypeName(): TypeName {
    return try {
        ClassName(sprouteRoot.java.packageName, sprouteRoot.java.simpleName)
    } catch (exception: MirroredTypeException) {
        exception.typeMirror.asTypeName()
    }
}
