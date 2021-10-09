package com.casadetasha.kexp.sproute.processor.ktx

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName

internal fun CodeBlock.Builder.addMethodParameters(methodParams: List<MemberName>?) = apply {
    methodParams.letIfNotEmpty {
        val memberParamString = it.joinToString(", ") { "%M" }
        val parameters = "($memberParamString)"
        add(parameters, *it.toTypedArray())
    }.orElseRun {
        add("()")
    }
}
