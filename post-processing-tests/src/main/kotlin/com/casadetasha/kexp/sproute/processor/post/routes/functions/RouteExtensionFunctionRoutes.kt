package com.casadetasha.kexp.sproute.processor.post.routes.functions

import com.casadetasha.kexp.sproute.annotations.*
import io.ktor.server.routing.*

@Get("/route_extension_function_routes")
fun Route.getRouteExtensionFunction() = "Route Extension function GOTTED!"

@Post("/route_extension_function_routes")
fun Route.postRouteExtensionFunction() = "Route Extension function POSTED!"

@Patch("/route_extension_function_routes")
fun Route.patchRouteExtensionFunction() = "Route Extension function PATCHED!"

@Put("/route_extension_function_routes")
fun Route.putRouteExtensionFunction() = "Route Extension function PUTTED!"

@Delete("/route_extension_function_routes")
fun Route.deleteRouteExtensionFunction() = "Route Extension function DELETED!"

@Head("/route_extension_function_routes")
fun Route.headRouteExtensionFunction() = "Route Extension function HEADED!"

@Options("/route_extension_function_routes")
fun Route.optionsRouteExtensionFunction() = "Route Extension function OPTIONED!"
