package com.casadetasha.kexp.sproute.processor.post.routes.segment

import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.processor.post.SubPackageRouteRoot

@Sproute("/routes", sprouteRoot = SubPackageRouteRoot::class)
internal class SubPackageRootedRoutes {
    @Get
    fun boringGet() = "Sub package route boring route GET."

    @Get("/amended_segment")
    fun amendedSegmentGet() : String = "Sub package route slightly less boring amended route GET!"

    @Get("/renamed_routes", includeClassRouteSegment = false)
    fun renamedSegmentGet() : String = "Sub package route slightly less boring replaced route GET, oh ya ya ya yaaaa!"
}