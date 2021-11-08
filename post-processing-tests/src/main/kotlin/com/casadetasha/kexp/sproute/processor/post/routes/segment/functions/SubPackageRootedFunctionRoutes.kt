package com.casadetasha.kexp.sproute.processor.post.routes.segment.functions

import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.post.SubPackageRouteRoot

@Get("/request_routes2")
@Sproute(sprouteRoot = SubPackageRouteRoot::class)
fun getWithRouteInRequest() = "GOT from a sub package function with route in request."

@Get
@Sproute("/sproute_routes", sprouteRoot = SubPackageRouteRoot::class)
fun getWithRouteInSproute() = "GOT from a sub package function with route in sproute."

@Get("/request_routes")
@Sproute("/sproute", sprouteRoot = SubPackageRouteRoot::class)
fun getWithRouteInBoth() = "GOT from a sub package function with route in sproute and request."
