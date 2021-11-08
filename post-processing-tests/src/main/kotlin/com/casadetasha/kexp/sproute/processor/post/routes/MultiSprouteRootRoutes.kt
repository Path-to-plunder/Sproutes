package com.casadetasha.kexp.sproute.processor.post.routes

import com.casadetasha.kexp.sproute.annotations.*

@Sproute("/multi")
class FirstStageRoute {
    @Get fun get() = "Multi get"
}

@Sproute("/root", sprouteRoot = FirstStageRoute::class)
class SecondStageRoute {
    @Get fun get() = "Multi root get"
}

@Sproute("/sproute", sprouteRoot = SecondStageRoute::class)
class ThirdStageRoute {
    @Get fun get() = "Multi root sproute get"
}
