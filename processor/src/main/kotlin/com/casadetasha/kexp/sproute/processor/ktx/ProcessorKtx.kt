package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.annotationparser.kxt.getClassesAnnotatedWith
import com.casadetasha.kexp.annotationparser.kxt.getFileFacadesForTopLevelFunctionsAnnotatedWith
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.models.AnnotatedSprouteRoot
import com.casadetasha.kexp.sproute.processor.models.Root
import com.casadetasha.kexp.sproute.processor.models.Root.Companion.defaultRoot
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication.BaseAuthentication
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteParent
import com.casadetasha.kexp.sproute.processor.models.objects.KotlinNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.models.objects.SprouteRequestAnnotations.validRequestTypes
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

internal fun RoundEnvironment.getSprouteRoots(): Map<String, Root> =
    HashMap<String, Root>().apply {
        val baseAuthentication = BaseAuthentication()
        SprouteRoot::class.asTypeName().let {
            defaultRoot = AnnotatedSprouteRoot(
                typeName = it,
                packageName = it.packageName,
                routeSegment = "",
                canAppendPackage = false,
                sprouteAuthentication = baseAuthentication
            )

            this[it.toString()] = defaultRoot
        }

        getClassesAnnotatedWith(SprouteRoot::class).forEach {
            val annotation = it.element.getAnnotation(SprouteRoot::class.java)!!
            this[it.className.toString()] = AnnotatedSprouteRoot(
                it.className,
                it.className.packageName,
                annotation.rootSprouteSegment,
                annotation.appendSubPackagesAsSegments,
                baseAuthentication.createChildFromElement(it.element)
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
            val sprouteRoot = classSprouteAnnotation.getSprouteRoot()
            val auth = sprouteRoot.sprouteAuthentication.createChildFromElement(it.element)

            SprouteParent.SprouteClass(
                classData = it.classData,
                primaryConstructorParams = it.primaryConstructorParams?.toRequestParamMemberNames(),
                sprouteAuthentication = auth,
                sprouteRoot = sprouteRoot,
                classRouteSegment = classSprouteAnnotation.routeSegment,
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray()),
            )
        }
        .toSet()
