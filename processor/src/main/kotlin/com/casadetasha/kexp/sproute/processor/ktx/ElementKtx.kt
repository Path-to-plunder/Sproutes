package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import kotlin.reflect.KClass

internal fun Element.getRequestMethods(): Map<String, ExecutableElement> {
    val requestMap: HashMap<String, ExecutableElement> = HashMap()
    SprouteRequestAnnotations.validRequestTypes.forEach {
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

internal fun Element.hasAnnotation(clazz: Class<out Annotation>): Boolean {
    return getAnnotation(clazz) != null
}

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
