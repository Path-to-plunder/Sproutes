package com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers

import com.casadetasha.kexp.annotationparser.KotlinValue.KotlinFunction
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.ktx.getSprouteRootKey
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.models.Root
import com.casadetasha.kexp.sproute.processor.models.Root.Companion.defaultRoot
import com.casadetasha.kexp.sproute.processor.models.Root.Companion.sprouteRoots
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
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
    internal open class SprouteClass(
        private val parentRootKey: TypeName,
        override val rootKey: TypeName,
        val classData: ClassData,
        val primaryConstructorParams: List<MemberName>?,
        val classRouteSegment: String,
        functions: Set<KotlinFunction>
    ) : SprouteParent(
        packageName = classData.className.packageName,
        classSimpleName = classData.className.simpleName
    ), Root {

        private var sprouteRoot: Root? = null
        private val validatedSprouteRoot: Root @Synchronized get() {
            if (parentRootKey == classData.className) processingEnvironment.printThenThrowError("A Sproute cannot be its own parent")
            if (sprouteRoot != null) return sprouteRoot!!

            sprouteRoot = sprouteRoots[parentRootKey] ?: defaultRoot
            if (sprouteRoot is SprouteClass) (sprouteRoot as SprouteClass).validateRootKeyDoesNotExistInParents(rootKey)

            return sprouteRoot!!
        }

        private val rootPathSegment by lazy {
            validatedSprouteRoot.getSproutePathForPackage(packageName)
        }

        private var combinedRouteParentsCache: Set<TypeName>? = null
        private val parentRootKeys: Set<TypeName> by lazy {
            return@lazy if (combinedRouteParentsCache != null) {
                combinedRouteParentsCache!!
            } else if (parentRootKey == Sproute::class.asTypeName()) {
                setOf(parentRootKey)
            } else {
                (sprouteRoot as SprouteClass).parentRootKeys
            }
        }

        override val sprouteAuthentication: SprouteAuthentication by lazy {
            validatedSprouteRoot.sprouteAuthentication
        }

        override val sprouteRequestFunctions: Set<SprouteRequestFunction> by lazy {
            functions
                .map {
                    SprouteRequestFunction(
                        sprouteRootKey = parentRootKey,
                        kotlinFunction = it,
                        classRouteSegment = classRouteSegment
                    )
                }.toSet()
        }

        override fun getSproutePathForPackage(sproutePackage: String): String {
            return "${rootPathSegment}$classRouteSegment"
        }

        private fun validateRootKeyDoesNotExistInParents(childRootKey: TypeName) {
            processingEnvironment.printThenThrowError(
                "rootKey = $rootKey, childRootKey = $childRootKey, parentRootKeys = $parentRootKeys")
            val allRoots = parentRootKeys + rootKey
            if (allRoots.contains(childRootKey)) {
                processingEnvironment.printThenThrowError(
                    "Found cyclical Root dependency adding $childRootKey to root hierarchy" +
                            " ( ${allRoots.joinToString(", ")} )")
            }
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

                SprouteRequestFunction(
                    sprouteRootKey = classSprouteAnnotation?.getSprouteRootKey(),
                    kotlinFunction = it,
                    classRouteSegment = classSprouteAnnotation?.routeSegment ?: ""
                )
            }.toSet()
    }
}
