package com.casadetasha.kexp.sproute.processor.values

import com.squareup.kotlinpoet.MemberName

internal sealed class SprouteParameter

internal class SprouteMemberParameter(val memberName: MemberName): SprouteParameter()

internal class SproutePathParamParameter(val paramKey: String): SprouteParameter()

internal class SprouteQueryParamParameter(val paramKey: String): SprouteParameter()
