package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.annotationparser.kxt.FileFacadeParser
import com.casadetasha.kexp.annotationparser.kxt.getClassesAnnotatedWith
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SproutePackageRoot
import com.casadetasha.kexp.sproute.processor.models.sproutes.SproutePackage
import com.casadetasha.kexp.sproute.processor.models.KotlinNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestAnnotations.validRequestTypes
import com.casadetasha.kexp.sproute.processor.models.sproutes.SprouteClass
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.AnnotatedSprouteRoot
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.ProcessedSprouteRoots
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.SprouteRoot
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

internal fun RoundEnvironment.getSprouteRoots(): Map<TypeName, SprouteRoot> =
    HashMap<TypeName, SprouteRoot>().apply {
        getClassesAnnotatedWith(SproutePackageRoot::class).forEach {
            val className = it.className
            val annotation = it.element.getAnnotation(SproutePackageRoot::class.java)!!

            this[className] = AnnotatedSprouteRoot(
                childRootKey = it.className,
                packageName = className.packageName,
                routeSegment = annotation.rootSprouteSegment,
                canAppendPackage = annotation.appendSubPackagesAsSegments,
                sprouteAuthentication = ProcessedSprouteRoots.defaultSprouteRoot.sprouteAuthentication.createChildFromElement(it.element)
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
            val classSprouteAnnotation: Sproute = it.element.getAnnotation(Sproute::class.java)
            val sprouteRootKey = classSprouteAnnotation.getSprouteRootKey()

            SprouteClass(
                childRootKey = it.className,
                parentRootKey = sprouteRootKey,
                classData = it.classData,
                primaryConstructorParams = it.primaryConstructorParams?.toRequestParamMemberNames(),
                classRouteSegment = classSprouteAnnotation.routeSegment,
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray()),
            ).apply { ProcessedSprouteRoots.put(this) }
        }.toSet()
