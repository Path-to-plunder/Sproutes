package com.casadetasha.kexp.sproute.processor.post.routes.functions

import com.casadetasha.kexp.sproute.annotations.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

@SprouteRoot("/extension_function_routes")
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
