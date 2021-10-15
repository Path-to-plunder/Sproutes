package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.annotationparser.kxt.getClassesAnnotatedWith
import com.casadetasha.kexp.annotationparser.kxt.getFileFacadesForTopLevelFunctionsAnnotatedWith
import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.MemberNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.validRequestTypes
import com.casadetasha.kexp.sproute.processor.models.SprouteAuthentication.BaseAuthentication
import com.casadetasha.kexp.sproute.processor.models.SprouteKotlinParent
import com.casadetasha.kexp.sproute.processor.models.SprouteRootInfo
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment

internal fun RoundEnvironment.getSprouteRoots(): Map<TypeName, SprouteRootInfo> = HashMap<TypeName, SprouteRootInfo>()
    .apply {
        val baseAuthentication = BaseAuthentication()
        SprouteRoot::class.asTypeName().let {
            this[it] = SprouteRootInfo(
                packageName = it.packageName,
                routeSegment = "",
                canAppendPackage = false,
                sprouteAuthentication = baseAuthentication
            )
        }

        getClassesAnnotatedWith(SprouteRoot::class).forEach {
            val annotation = it.element.getAnnotation(SprouteRoot::class.java)!!
            this[it.className] = SprouteRootInfo(
                it.className.packageName,
                annotation.rootSprouteSegment,
                annotation.appendSubPackagesAsSegments,
                baseAuthentication.createChildFromElement(it.element)
            )
        }
    }.toMap()

@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.getRoutePackages(): Set<SprouteKotlinParent.SproutePackage> {
    return getFileFacadesForTopLevelFunctionsAnnotatedWith(validRequestTypes)
        .map {
            SprouteKotlinParent.SproutePackage(
                packageName = it.packageName,
                fileName = it.fileName,
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray())
            )
        }
        .toSet()
}

@OptIn(KotlinPoetMetadataPreview::class)
internal fun RoundEnvironment.getRouteClasses(): Set<SprouteKotlinParent.SprouteClass> =
    getClassesAnnotatedWith(Sproute::class)
        .map {
            val classSprouteAnnotation: Sproute = it.element.getAnnotation(Sproute::class.java)
            val authenticatedAnnotation: Authenticated? = it.element.getAnnotation(Authenticated::class.java)
            val sprouteRoot = classSprouteAnnotation.getSprouteRoot()
            val auth = sprouteRoot.sprouteAuthentication.createChildFromElement(it.element)

            SprouteKotlinParent.SprouteClass(
                classData = it.classData,
                primaryConstructorParams = it.primaryConstructorParams?.toRequestParamMemberNames(),
                sprouteAuthentication = auth,
                rootPathSegment = sprouteRoot.getPathPrefixToSproutePackage(it.packageName),
                classRouteSegment = classSprouteAnnotation.routeSegment,
                functions = it.getFunctionsAnnotatedWith(*validRequestTypes.toTypedArray()),
            )
        }
        .toSet()
