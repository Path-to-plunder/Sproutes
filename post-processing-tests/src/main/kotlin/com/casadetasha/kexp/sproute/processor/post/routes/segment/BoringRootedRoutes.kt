package com.casadetasha.kexp.sproute.processor.post.routes.segment

import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.post.BoringRouteRoot

@Sproute("/routes", sprouteRoot = BoringRouteRoot::class)
internal class BoringRootedRoutes {
    @Get
    fun boringGet() = "Boring rooted route boring route GET."

    @Get("/amended_segment")
    fun amendedSegmentGet() : String = "Boring rooted route slightly less boring amended route GET!"

    @Get("/renamed_routes")
    fun renamedSegmentGet() : String =
        "Boring rooted route slightly less boring replaced route GET, estoy ahogado en un baaaaaaar!"
}
