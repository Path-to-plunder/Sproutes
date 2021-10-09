package com.casadetasha.kexp.sproute.processor.post.routes

import com.casadetasha.kexp.sproute.annotations.*

@Sproute("/amended_routes_segment")
class AmendedRequestRoutes {
    @Get("/amended_routes")
    fun get() = "Amended GET."

    @Post("/amended_routes")
    fun post() = "Amended POST."

    @Put("/amended_routes")
    fun put() = "Amended PUT."

    @Patch("/amended_routes")
    fun patch() = "Amended PATCH."

    @Delete("/amended_routes")
    fun delete() = "Amended DELETE."

    @Head("/amended_routes")
    fun head() = "Amended HEAD."

    @Options("/amended_routes")
    fun options() = "Amended OPTIONS."
}
