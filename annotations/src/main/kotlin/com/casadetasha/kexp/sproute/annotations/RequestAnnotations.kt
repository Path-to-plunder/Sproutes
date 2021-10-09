package com.casadetasha.kexp.sproute.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Get(val routeSegment: String = "", val includeClassRouteSegment: Boolean = true)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Post(val routeSegment: String = "", val includeClassRouteSegment: Boolean = true)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Put(val routeSegment : String = "", val includeClassRouteSegment : Boolean = true)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Patch(val routeSegment : String = "", val includeClassRouteSegment : Boolean = true)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Delete(val routeSegment : String = "", val includeClassRouteSegment : Boolean = true)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Head(val routeSegment : String = "", val includeClassRouteSegment : Boolean = true)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Options(val routeSegment : String = "", val includeClassRouteSegment : Boolean = true)
