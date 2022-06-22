package com.casadetasha.kexp.sproute.processor.post.routes

import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import io.ktor.server.application.*
import io.ktor.server.response.*


@Sproute("/routes/parameterized")
class ParameterizedRoutes(private val call: ApplicationCall, private val application: Application) {

    @Get("/call")
    suspend fun getCall(call: ApplicationCall) {
        call.respond("Responded from method call param")
    }

    @Get("/application")
    fun getApplication(application: Application): String {
        return "My root path is ${application.environment.rootPath}"
    }

    @Get("/class_call")
    suspend fun getClassCall() {
        return call.respond("Responded from class call param")
    }

    @Get("/class_application")
    fun getClassApplication(): String {
        return "My root path is ${application.environment.rootPath}"
    }
}