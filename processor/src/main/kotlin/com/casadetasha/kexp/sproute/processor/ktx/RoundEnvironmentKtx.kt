package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.SprouteRoot
import com.casadetasha.kexp.sproute.processor.models.SprouteRootInfo
import com.google.common.collect.ImmutableMap
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.RoundEnvironment

internal fun RoundEnvironment.getSprouteRoots() :
        ImmutableMap<TypeName, SprouteRootInfo> = with(HashMap<TypeName, SprouteRootInfo>()) {
    SprouteRoot::class.asTypeName().let {
        this[it] = SprouteRootInfo(it.packageName, "", canAppendPackage = false)
    }

    getElementsAnnotatedWith(SprouteRoot::class.java)?.forEach { classElement ->
        classElement.getAnnotation(SprouteRoot::class.java).let {
            val className = classElement.getClassName()
            this[className] = SprouteRootInfo(className.packageName, it.rootSprouteSegment, it.appendSubPackagesAsSegments)
        }
    }

    ImmutableMap.copyOf(this)
}
