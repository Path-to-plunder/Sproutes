package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.annotationparser.kxt.getClassesAnnotatedWith
import com.casadetasha.kexp.annotationparser.kxt.getFileFacadesForTopLevelFunctionsAnnotatedWith
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SproutePackageRoot
import com.casadetasha.kexp.sproute.processor.models.AnnotatedSprouteRoot
import com.casadetasha.kexp.sproute.processor.models.DefaultSprouteRoot
import com.casadetasha.kexp.sproute.processor.models.Root
import com.casadetasha.kexp.sproute.processor.models.Root.Companion.defaultRoot
import com.casadetasha.kexp.sproute.processor.models.Root.Companion.sprouteRoots
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteParent
import com.casadetasha.kexp.sproute.processor.models.objects.KotlinNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.models.objects.SprouteRequestAnnotations.validRequestTypes
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
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

internal fun RoundEnvironment.getSprouteRoots(): Map<TypeName, Root> =
    HashMap<TypeName, Root>().apply {
        defaultRoot = DefaultSprouteRoot(Sproute::class.asTypeName())
        this[defaultRoot.rootKey] = defaultRoot

        getClassesAnnotatedWith(SproutePackageRoot::class).forEach {
            val className = it.className
            val annotation = it.element.getAnnotation(SproutePackageRoot::class.java)!!

            this[className] = AnnotatedSprouteRoot(
                rootKey = it.className,
                packageName = className.packageName,
                routeSegment = annotation.rootSprouteSegment,
                canAppendPackage = annotation.appendSubPackagesAsSegments,
                sprouteAuthentication = defaultRoot.sprouteAuthentication.createChildFromElement(it.element)
            )
        }
    }.toMap()

@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.getRoutePackages(): Set<SprouteParent.SproutePackage> {
    return getFileFacadesForTopLevelFunctionsAnnotatedWith(validRequestTypes)
        .map {
            SprouteParent.SproutePackage(
                packageName = it.packageName,
                fileName = it.fileName,
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray())
            )
        }
        .toSet()
}

@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.getRouteClasses(): Set<SprouteParent.SprouteClass> =
    getClassesAnnotatedWith(Sproute::class)
        .map {
            val classSprouteAnnotation: Sproute = it.element.getAnnotation(Sproute::class.java)
            val sprouteRootKey = classSprouteAnnotation.getSprouteRootKey()

            SprouteParent.SprouteClass(
                rootKey = it.className,
                parentRootKey = sprouteRootKey,
                classData = it.classData,
                primaryConstructorParams = it.primaryConstructorParams?.toRequestParamMemberNames(),
                classRouteSegment = classSprouteAnnotation.routeSegment,
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray()),
            ).apply { sprouteRoots[it.className] = this }
        }.toSet()
