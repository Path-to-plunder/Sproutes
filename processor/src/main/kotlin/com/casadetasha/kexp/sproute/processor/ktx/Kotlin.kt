package com.casadetasha.kexp.sproute.processor.ktx

import com.casadetasha.kexp.sproute.annotations.*
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

internal fun Boolean.orElse(function: () -> Unit) {
    if (!this) function()
}

internal fun String.asPath() : String {
    return this.replace(".", "/")
}

internal fun String.asSubPackageOf(classPackage: String) : String {
    return this.removePrefix(classPackage)
}

internal fun String.asMethod() : String {
    return this.asPath().replace("/", "_")
}

internal fun List<String>.asVarArgs(): String = this.let {
    return joinToString(", ") { "\"$it\"" }
}

internal fun <T> List<T>?.ifNotEmpty(function: (List<T>) -> Unit): Boolean {
    if (this == null || isEmpty()) {
        return false
    }

    function(this)
    return true
}

internal fun KClass<*>.asCanonicalName(): String = asTypeName().canonicalName

internal fun KClass<*>.toMemberName(): MemberName {
    return MemberName(asClassName().packageName, asClassName().simpleName)
}

internal fun Annotation.asKClass(): KClass<out Annotation> {
    return when(this){
        is Get -> Get::class
        is Post -> Post::class
        is Put -> Put::class
        is Patch -> Patch::class
        is Delete -> Delete::class
        is Head -> Head::class
        is Options -> Options::class
        else -> throw IllegalArgumentException("Provided annotation must be one of the types in validRequestList")
    }
}
