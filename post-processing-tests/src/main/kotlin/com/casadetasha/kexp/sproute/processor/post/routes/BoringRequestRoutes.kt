package com.casadetasha.kexp.sproute.processor.post.routes

import com.casadetasha.kexp.sproute.annotations.*

@Sproute("/boring_request_routes")
class BoringRequestRoutes {
    @Get
    fun get() = "Boring GET."

    @Post
    fun post() = "Boring POST."

    @Put
    fun put() = "Boring PUT."

    @Patch
    fun patch() = "Boring PATCH."

    @Delete
    fun delete() = "Boring DELETE."

    @Head
    fun head() = "Boring HEAD."

    @Options
    fun options() = "Boring OPTIONS."
}
