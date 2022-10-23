package com.casadetasha.kexp.sproute.processor.post.routes.segment.functions

import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.PathParam
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.post.BoringRouteRoot

@Get("/request_routes")
@Sproute(sprouteRoot = BoringRouteRoot::class)
fun boringGetWithRouteInRequest() = "GOT from a boring function with route in request."

@Get
@Sproute("/sproute_routes", sprouteRoot = BoringRouteRoot::class)
fun boringGetWithRouteInSproute() = "GOT from a boring function with route in sproute."

@Get("/request_routes")
@Sproute("/sproute", sprouteRoot = BoringRouteRoot::class)
fun boringGetWithRouteInBoth() = "GOT from a boring function with route in sproute and request."


@Sproute("/home/users")
public class UserRouteThingy() {

}

@Sproute("/{id}/avatar", sprouteRoot = UserRouteThingy::class)
@Get
fun get() {

}