package com.casadetasha.kexp.sproute.processor.post.routes

import com.casadetasha.kexp.sproute.annotations.*

@Sproute("/suspend_request_routes")
class SuspendRequestRoutes {
    @Get
    suspend fun get() = "Suspend GET."

    @Post
    suspend fun post() = "Suspend POST."

    @Put
    suspend fun put() = "Suspend PUT."

    @Patch
    suspend fun patch() = "Suspend PATCH."

    @Delete
    suspend fun delete() = "Suspend DELETE."

    @Head
    suspend fun head() = "Suspend HEAD."

    @Options
    suspend fun options() = "Suspend OPTIONS."
}
