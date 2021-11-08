package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.annotationparser.kxt.FileFacadeParser
import com.casadetasha.kexp.annotationparser.kxt.getClassesAnnotatedWith
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SproutePackageRoot
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.annotation_bridge.SprouteRequestAnnotationBridge.validRequestTypes
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteClass
import com.casadetasha.kexp.sproute.processor.sproutes.SproutePackage
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.AuthLazyLoader
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.BaseAuthentication
import com.casadetasha.kexp.sproute.processor.sproutes.segments.*
import com.casadetasha.kexp.sproute.processor.sproutes.segments.ProcessedRouteSegments
import com.casadetasha.kexp.sproute.processor.sproutes.segments.RouteSegment
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.tools.Diagnostic

internal fun ProcessingEnvironment.printNote(noteText: String) {
    messager.printMessage(Diagnostic.Kind.NOTE, noteText)
}

internal fun ProcessingEnvironment.printThenThrowError(errorMessage: String): Nothing {
    messager.printMessage(Diagnostic.Kind.ERROR, errorMessage)
    throw IllegalArgumentException(errorMessage)
}

internal fun RoundEnvironment.getSprouteRoots(): Map<TypeName, RouteSegment> =
    HashMap<TypeName, RouteSegment>().apply {
        getClassesAnnotatedWith(SproutePackageRoot::class).forEach {
            val className = it.className
            val annotation = it.element.getAnnotation(SproutePackageRoot::class.java)!!

            this[className] = LeadingRouteSegment.AnnotatedRouteSegment(
                segmentKey = it.className,
                packageName = className.packageName,
                routeSegment = annotation.rootSprouteSegment,
                authentication = BaseAuthentication().createChildFromElement(it.element)
            )
        }
    }.toMap()


@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.generateRoutePackages(): Set<SproutePackage> {
    return FileFacadeParser(this)
        .getFacadesForFilesContainingAnnotations(validRequestTypes)
        .map {
            SproutePackage(
                packageName = it.packageName,
                fileName = it.fileName,
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray())
            )
        }
        .toSet()
}

@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.generateRouteClasses(): Set<SprouteClass> =
    getClassesAnnotatedWith(Sproute::class)
        .map {
            val sprouteAnnotation: Sproute = it.element.getAnnotation(Sproute::class.java)
            val sprouteRootKey = sprouteAnnotation.getSprouteRootKey()

            val segment = TrailingRouteSegment(
                routeSegment = sprouteAnnotation.routeSegment,
                parentSegmentKey = sprouteRootKey,
                segmentKey = it.className,
                authLazyLoader = AuthLazyLoader(sprouteRootKey, it.element)
            ).apply { ProcessedRouteSegments.put(this) }

            SprouteClass(
                classData = it.classData,
                primaryConstructorParams = it.primaryConstructorParams?.toRequestParamMemberNames(),
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray()),
                routeSegment = segment
            )
        }.toSet()
