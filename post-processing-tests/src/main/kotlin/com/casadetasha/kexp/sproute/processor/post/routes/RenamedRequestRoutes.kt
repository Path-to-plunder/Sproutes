package com.casadetasha.kexp.sproute.processor.post.routes

import com.casadetasha.kexp.sproute.annotations.*

@Sproute("/original_name_routes")
class RenamedRequestRoutes {
    @Get("/renamed_routes", includeClassRouteSegment = false)
    fun get() = "Renamed GET."

    @Post("/renamed_routes", includeClassRouteSegment = false)
    fun post() = "Renamed POST."

    @Put("/renamed_routes", includeClassRouteSegment = false)
    fun put() = "Renamed PUT."

    @Patch("/renamed_routes", includeClassRouteSegment = false)
    fun patch() = "Renamed PATCH."

    @Delete("/renamed_routes", includeClassRouteSegment = false)
    fun delete() = "Renamed DELETE."

    @Head("/renamed_routes", includeClassRouteSegment = false)
    fun head() = "Renamed HEAD."

    @Options("/renamed_routes", includeClassRouteSegment = false)
    fun options() = "Renamed OPTIONS."
}
