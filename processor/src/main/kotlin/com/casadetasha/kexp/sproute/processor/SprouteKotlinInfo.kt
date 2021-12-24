package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.annotationparser.AnnotationParser
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.SproutePackageRoot
import com.casadetasha.kexp.sproute.processor.annotation_bridge.SprouteRequestAnnotationBridge
import com.casadetasha.kexp.sproute.processor.ktx.getSprouteRootKey
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteClass
import com.casadetasha.kexp.sproute.processor.sproutes.SproutePackage
import com.casadetasha.kexp.sproute.processor.sproutes.SprouteParent
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.AuthLazyLoader
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.BaseAuthentication
import com.casadetasha.kexp.sproute.processor.sproutes.segments.LeadingRouteSegment
import com.casadetasha.kexp.sproute.processor.sproutes.segments.ProcessedRouteSegments
import com.casadetasha.kexp.sproute.processor.sproutes.segments.RouteSegment
import com.casadetasha.kexp.sproute.processor.sproutes.segments.TrailingRouteSegment
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.toRequestParamMemberNames
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview

internal class SprouteKotlinInfo private constructor(
    val sprouteParents: Set<SprouteParent>,
    val sproutePackageRoots: Map<TypeName, RouteSegment>
) {

    companion object {
        fun load(): SprouteKotlinInfo = SprouteKotlinInfo(
            sprouteParents = getRouteClasses().toSortedSet() + getRoutePackages().toSortedSet(),
            sproutePackageRoots = getSprouteRoots()
        )

        @OptIn(KotlinPoetMetadataPreview::class)
        private fun getRoutePackages(): Set<SproutePackage> {
            return AnnotationParser.getFileFacadesForTopLevelFunctionsAnnotatedWith(SprouteRequestAnnotationBridge.validRequestTypes)
                .map {
                    SproutePackage(
                        packageName = it.packageName,
                        fileName = it.fileName,
                        functions = it.getFunctionsAnnotatedWith(
                            *SprouteRequestAnnotationBridge.validRequestTypes.toTypedArray())
                    )
                }
                .toSet()
        }

        @OptIn(KotlinPoetMetadataPreview::class)
        private fun getRouteClasses(): Set<SprouteClass> =
            AnnotationParser.getClassesAnnotatedWith(Sproute::class)
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
                        functions = it.getFunctionsAnnotatedWith(*SprouteRequestAnnotationBridge.validRequestTypes.toTypedArray()),
                        routeSegment = segment
                    )
                }.toSet()


        private fun getSprouteRoots(): Map<TypeName, RouteSegment> =
            HashMap<TypeName, RouteSegment>().apply {
                AnnotationParser.getClassesAnnotatedWith(SproutePackageRoot::class).forEach {
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
    }
}
