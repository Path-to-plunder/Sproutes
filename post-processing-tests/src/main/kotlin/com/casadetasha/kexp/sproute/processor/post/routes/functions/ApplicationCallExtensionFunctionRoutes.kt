package com.casadetasha.kexp.sproute.processor.post.routes.functions

import com.casadetasha.kexp.sproute.annotations.*
import io.ktor.application.*
import io.ktor.response.*

@Get("/application_call_extension_function_routes")
suspend fun ApplicationCall.getApplicationCallExtensionFunction() {
    respond("ApplicationCall Extension function GOTTED!")
}

@Post("/application_call_extension_function_routes")
suspend fun ApplicationCall.postApplicationCallExtensionFunction() {
    respond("ApplicationCall Extension function POSTED!")
}

@Patch("/application_call_extension_function_routes")
suspend fun ApplicationCall.patchApplicationCallExtensionFunction() {
    respond("ApplicationCall Extension function PATCHED!")
}

@Put("/application_call_extension_function_routes")
suspend fun ApplicationCall.putApplicationCallExtensionFunction() {
    respond("ApplicationCall Extension function PUTTED!")
}

@Delete("/application_call_extension_function_routes")
suspend fun ApplicationCall.deleteApplicationCallExtensionFunction() {
    respond("ApplicationCall Extension function DELETED!")
}

@Head("/application_call_extension_function_routes")
suspend fun ApplicationCall.headApplicationCallExtensionFunction() {
    respond("ApplicationCall Extension function HEADED!")
}

@Options("/application_call_extension_function_routes")
suspend fun ApplicationCall.optionsApplicationCallExtensionFunction() {
    respond("ApplicationCall Extension function OPTIONED!")
}
