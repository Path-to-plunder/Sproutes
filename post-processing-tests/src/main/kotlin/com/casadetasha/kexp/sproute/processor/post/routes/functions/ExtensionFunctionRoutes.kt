package com.casadetasha.kexp.sproute.processor.post.routes.functions

import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Sproute("/extension_function_routes")
interface ExtensionFunctionRoot

@Get("/application_call")
@Sproute(sprouteRoot = ExtensionFunctionRoot::class)
suspend fun ApplicationCall.getApplicationCallExtensionFunction() {
    respond("This is a responded through the application call I'm extending!")
}

@Get("/route")
@Sproute(sprouteRoot = ExtensionFunctionRoot::class)
fun Route.postApplicationCallExtensionFunction(): String {
    return "My route is $this"
}
