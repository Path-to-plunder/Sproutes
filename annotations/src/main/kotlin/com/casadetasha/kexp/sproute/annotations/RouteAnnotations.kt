package com.casadetasha.kexp.sproute.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Sproute(val routeSegment: String = "", val sprouteRoot: KClass<out Any> = SprouteRoot::class)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class SprouteRoot(val rootSprouteSegment: String = "", val appendSubPackagesAsSegments: Boolean = false)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Authenticated(val name: String = "")

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Unauthenticated
