package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.annotationparser.kxt.FileFacadeParser
import com.casadetasha.kexp.annotationparser.kxt.getClassesAnnotatedWith
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SproutePackageRoot
import com.casadetasha.kexp.sproute.processor.models.KotlinNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestAnnotations.validRequestTypes
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteClass
import com.casadetasha.kexp.sproute.processor.models.sproutes.SproutePackage
import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.AuthLazyLoader
import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.BaseAuthentication
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.*
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.AnnotatedSprouteSegment
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.ProcessedSprouteSegments
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.SprouteSegment
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

internal fun RoundEnvironment.getSprouteRoots(): Map<TypeName, SprouteSegment> =
    HashMap<TypeName, SprouteSegment>().apply {
        getClassesAnnotatedWith(SproutePackageRoot::class).forEach {
            val className = it.className
            val annotation = it.element.getAnnotation(SproutePackageRoot::class.java)!!

            this[className] = AnnotatedSprouteSegment(
                segmentKey = it.className,
                packageName = className.packageName,
                routeSegment = annotation.rootSprouteSegment,
                canAppendPackage = annotation.appendSubPackagesAsSegments,
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

            val segment = TrailingSprouteSegment(
                routeSegment = sprouteAnnotation.routeSegment,
                parentRootKey = sprouteRootKey,
                segmentKey = it.className,
                authLazyLoader = AuthLazyLoader(sprouteRootKey, it.element)
            ).apply { ProcessedSprouteSegments.put(this) }

            SprouteClass(
                classData = it.classData,
                primaryConstructorParams = it.primaryConstructorParams?.toRequestParamMemberNames(),
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray()),
                sprouteSegment = segment
            )
        }.toSet()
