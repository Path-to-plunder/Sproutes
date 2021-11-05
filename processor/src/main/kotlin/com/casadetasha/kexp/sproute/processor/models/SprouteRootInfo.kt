package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.ktx.asPath
import com.casadetasha.kexp.sproute.processor.ktx.asSubPackageOf
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers.SprouteAuthentication
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.type.MirroredTypeException

internal data class SprouteRootInfo(
    val packageName: String,
    val routeSegment: String,
    val canAppendPackage: Boolean,
    val sprouteAuthentication: SprouteAuthentication
) {

    companion object {
        internal val sprouteRoots: MutableMap<TypeName, SprouteRootInfo> = HashMap()
    }

    internal fun getPathPrefixToSproutePackage(sproutePackage: String): String {
        return if (canAppendPackage) {
            routeSegment + sproutePackage.asSubPackageOf(packageName).asPath().lowercase()
        } else routeSegment
    }
}
