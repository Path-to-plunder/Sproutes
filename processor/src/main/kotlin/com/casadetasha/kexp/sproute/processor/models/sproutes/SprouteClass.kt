package com.casadetasha.kexp.sproute.processor.models.sproutes

import com.casadetasha.kexp.annotationparser.KotlinValue
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.ProcessedSprouteRoots.getSprouteRootForChild
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.SprouteRoot
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData

@OptIn(KotlinPoetMetadataPreview::class)
internal open class SprouteClass(
    override val childRootKey: TypeName,
    val classData: ClassData,
    val primaryConstructorParams: List<MemberName>?,
    val classRouteSegment: String,
    parentRootKey: TypeName,
    functions: Set<KotlinValue.KotlinFunction>
) : SprouteParent(
    packageName = classData.className.packageName,
    classSimpleName = classData.className.simpleName
), SprouteRoot {

    private val parentRoot: SprouteRoot by lazy { getSprouteRootForChild(parentRootKey, childRootKey) }

    private val parentRootKeys: Set<TypeName> by lazy {
        if (parentRootKey == Sproute::class.asTypeName()) {
            setOf(parentRootKey)
        } else {
            (parentRoot as SprouteClass).parentRootKeys
        }
    }

    private val rootPathSegment by lazy {
        parentRoot.getSproutePathForPackage(packageName)
    }

    override val sprouteAuthentication: SprouteAuthentication by lazy {
        parentRoot.sprouteAuthentication
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

    override fun failIfChildRootIsCyclical(childRootKey: TypeName) {
        val allRoots = parentRootKeys + this.childRootKey
        if (allRoots.contains(childRootKey)) {
            processingEnvironment.printThenThrowError(
                "Found cyclical Root dependency adding $childRootKey to root hierarchy" +
                        " ( ${allRoots.joinToString(", ")} )")
        }
    }
}
