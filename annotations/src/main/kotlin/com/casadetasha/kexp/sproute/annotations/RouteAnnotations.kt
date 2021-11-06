package com.casadetasha.kexp.sproute.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Sproute(val routeSegment: String = "", val sprouteRoot: KClass<out Any> = Sproute::class)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class SproutePackageRoot(val rootSprouteSegment: String = "", val appendSubPackagesAsSegments: Boolean)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Authenticated(vararg val names: String, val optional: Boolean = false)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Unauthenticated
