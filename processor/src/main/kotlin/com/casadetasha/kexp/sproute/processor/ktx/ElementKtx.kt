package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import javax.lang.model.element.Element

internal fun Element.getTopLevelFunctionPathRoot(): String {
    val sprouteAnnotation = getAnnotation(Sproute::class.java)
    val sprouteRootSegment = sprouteAnnotation?.getSprouteRoot()?.getPathPrefixToSproutePackage(packageName) ?: ""
    val sprouteSegment = sprouteAnnotation?.routeSegment ?: ""

    return sprouteRootSegment + sprouteSegment
}

internal val Element.packageName: String
    get() {
        val packageElement = processingEnvironment.elementUtils.getPackageOf(this)
        return processingEnvironment.elementUtils.getPackageOf(packageElement).qualifiedName.toString()
    }
