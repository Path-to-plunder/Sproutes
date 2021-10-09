package com.casadetasha.kexp.sproute.processor.post.routes.functions

import com.casadetasha.kexp.sproute.annotations.*

@Sproute("/class_and_function_routes")
class ClassAndFunctionRoutes {
    @Get("/inside_function")
    fun getInsideClassRoute() = "Inside class GOTTED!"

    @Post("/inside_function")
    fun postInsideClassRoute() = "Inside class POSTED!"

    @Patch("/inside_function")
    fun patchInsideClassRoute() = "Inside class PATCHED!"

    @Put("/inside_function")
    fun putInsideClassRoute() = "Inside class PUTTED!"

    @Delete("/inside_function")
    fun deleteInsideClassRoute() = "Inside class DELETED!"

    @Head("/inside_function")
    fun headInsideClassRoute() = "Inside class HEADED!"

    @Options("/inside_function")
    fun optionsInsideClassRoute() = "Inside class OPTIONED!"
}

@Get("/class_and_function_routes/outside_function")
fun getClassAndFunctionRoutes() = "Outside class GOTTED!"

@Post("/class_and_function_routes/outside_function")
fun postClassAndFunctionRoutes() = "Outside class POSTED!"

@Patch("/class_and_function_routes/outside_function")
fun patchClassAndFunctionRoutes() = "Outside class PATCHED!"

@Put("/class_and_function_routes/outside_function")
fun putClassAndFunctionRoutes() = "Outside class PUTTED!"

@Delete("/class_and_function_routes/outside_function")
fun deleteClassAndFunctionRoutes() = "Outside class DELETED!"

@Head("/class_and_function_routes/outside_function")
fun headClassAndFunctionRoutes() = "Outside class HEADED!"

@Options("/class_and_function_routes/outside_function")
fun optionsClassAndFunctionRoutes() = "Outside class OPTIONED!"
