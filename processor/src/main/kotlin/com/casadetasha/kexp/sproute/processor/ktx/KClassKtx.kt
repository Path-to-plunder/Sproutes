package com.casadetasha.kexp.sproute.processor.ktx

import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

internal fun KClass<*>.asCanonicalName(): String = asTypeName().canonicalName

internal fun KClass<*>.toMemberName(): MemberName {
    return MemberName(asClassName().packageName, asClassName().simpleName)
}
