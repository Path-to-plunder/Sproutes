package com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers

import com.casadetasha.kexp.annotationparser.KotlinValue.KotlinFunction
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.ktx.getSprouteRoot
import com.casadetasha.kexp.sproute.processor.models.Root
import com.casadetasha.kexp.sproute.processor.models.Root.Companion.defaultRoot
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData

internal sealed class SprouteParent(
    val packageName: String,
    val classSimpleName: String
) : Comparable<SprouteParent> {

    val memberName = MemberName(packageName, classSimpleName)

    abstract val sprouteRequestFunctions: Set<SprouteRequestFunction>

    override fun compareTo(other: SprouteParent): Int {
        return this.memberName.toString().compareTo(other.memberName.toString())
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    internal class SprouteClass(
        val classData: ClassData,
        val primaryConstructorParams: List<MemberName>?,
        val classRouteSegment: String,
        override val sprouteAuthentication: SprouteAuthentication,
        functions: Set<KotlinFunction>,
        sprouteRoot: Root
    ) : SprouteParent(
        packageName = classData.className.packageName,
        classSimpleName = classData.className.simpleName
    ), Root {

        override val key: String = classData.className.toString()
        private val rootPathSegment = sprouteRoot.getSproutePathForPackage(packageName)

        override val sprouteRequestFunctions: Set<SprouteRequestFunction> = functions
            .map {
                SprouteRequestFunction(
                    sprouteRoot = sprouteRoot,
                    kotlinFunction = it,
                    classRouteSegment = classRouteSegment,
                    sprouteAuthentication = sprouteAuthentication.createChildFromElement(it.element)
                )
            }.toSortedSet()

        override fun getSproutePathForPackage(sproutePackage: String): String {
            return "${rootPathSegment}$classRouteSegment"
        }
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    internal class SproutePackage(
        packageName: String,
        fileName: String,
        functions: Set<KotlinFunction>
    ) : SprouteParent(
        packageName = packageName,
        classSimpleName = fileName
    ) {

        override val sprouteRequestFunctions: Set<SprouteRequestFunction> = functions
            .map {
                val classSprouteAnnotation: Sproute? = it.element.getAnnotation(Sproute::class.java)
                val sprouteRoot: Root = classSprouteAnnotation?.getSprouteRoot() ?: defaultRoot
                val auth = sprouteRoot.sprouteAuthentication.createChildFromElement(it.element)

                SprouteRequestFunction(
                    sprouteRoot = sprouteRoot,
                    kotlinFunction = it,
                    classRouteSegment = classSprouteAnnotation?.routeSegment ?: "",
                    sprouteAuthentication = auth
                )
            }.toSortedSet()
    }
}
