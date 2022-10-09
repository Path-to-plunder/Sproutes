package com.casadetasha.kexp.sproute.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PathParam(val paramKey: String = "")

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryParam(val paramKey: String = "")
