package com.casadetasha.kexp.sproute.processor.post.routes.functions

import com.casadetasha.kexp.sproute.annotations.*

@Get("/standalone_function_routes")
fun getStandaloneFunction() = "Standalone function GOTTED!"

@Post("/standalone_function_routes")
fun postStandaloneFunction() = "Standalone function POSTED!"

@Patch("/standalone_function_routes")
fun patchStandaloneFunction() = "Standalone function PATCHED!"

@Put("/standalone_function_routes")
fun putStandaloneFunction() = "Standalone function PUTTED!"

@Delete("/standalone_function_routes")
fun deleteStandaloneFunction() = "Standalone function DELETED!"

@Head("/standalone_function_routes")
fun headStandaloneFunction() = "Standalone function HEADED!"

@Options("/standalone_function_routes")
fun optionsStandaloneFunction() = "Standalone function OPTIONED!"
