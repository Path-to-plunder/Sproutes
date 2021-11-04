package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.models.SprouteRootInfo
import com.casadetasha.kexp.sproute.processor.models.SprouteRootInfo.Companion.sprouteRoots
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.type.MirroredTypeException

internal fun Sproute.getSprouteRoot(): SprouteRootInfo {
    val routeRootTypeName = getRootTypeName()
    return sprouteRoots[routeRootTypeName] ?: processingEnvironment.printThenThrowError(
        "@SprouteRoot annotation was not found for provided class $routeRootTypeName"
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

