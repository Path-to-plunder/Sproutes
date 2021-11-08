package com.casadetasha.kexp.sproute.processor.annotation_bridge

import com.casadetasha.kexp.sproute.annotations.*
import com.casadetasha.kexp.sproute.processor.ktx.asKClass
import javax.lang.model.element.Element
import kotlin.reflect.KClass

internal object SprouteRequestAnnotationBridge {
    private enum class RequestType(val annotationKClass: KClass<out Annotation>, val methodName: String) {
        GET(Get::class, "get"),
        POST(Post::class, "post"),
        PUT(Put::class, "put"),
        PATCH(Patch::class, "patch"),
        DELETE(Delete::class, "delete"),
        HEAD(Head::class, "head"),
        OPTIONS(Options::class, "options")
    }

    val validRequestTypes: List<KClass<out Annotation>> = RequestType.values().map { it.annotationKClass }

    fun Element.getInstaRequestAnnotation(): Annotation {
        return getAnnotation(Get::class.java)
            ?: getAnnotation(Post::class.java)
            ?: getAnnotation(Put::class.java)
            ?: getAnnotation(Patch::class.java)
            ?: getAnnotation(Delete::class.java)
            ?: getAnnotation(Head::class.java)
            ?: getAnnotation(Options::class.java)
            ?: throw IllegalStateException("RequestFunction must be annotated by a InstaRoute request annotation.")
    }

    fun getRouteSegment(annotation: Annotation) : String {
        return when(annotation){
            is Get -> annotation.routeSegment
            is Post -> annotation.routeSegment
            is Put -> annotation.routeSegment
            is Patch -> annotation.routeSegment
            is Delete -> annotation.routeSegment
            is Head -> annotation.routeSegment
            is Options -> annotation.routeSegment
            else -> throw IllegalArgumentException("ProvidedKClass must be one of the types in validRequestList")
        }
    }

    fun getRequestMethodName(annotation: Annotation): String {
        val kclass = annotation.asKClass()
        val requestMethod = RequestType.values()
            .firstOrNull { it.annotationKClass == kclass }
            ?.methodName

        return requestMethod ?: throw IllegalArgumentException(
            "providedKClass must be one of the types in validRequestList")
    }
}
