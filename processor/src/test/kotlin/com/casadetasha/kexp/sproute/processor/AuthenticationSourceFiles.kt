package com.casadetasha.kexp.sproute.processor

import com.tschuchort.compiletesting.SourceFile

object AuthenticationSourceFiles {
    internal val boringRouteSource = SourceFile.kotlin(
        "BoringRouteSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Authenticated
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import com.casadetasha.kexp.sproute.annotations.SprouteRoot

            @SprouteRoot("/authenticated")
            interface AuthenticatedRoot

            @Sproute("/route")
            @Authenticated
            class Route {
                @Get("/get-a-route", includeClassRouteSegment = false)
                fun get() = "get me"
            }
        """.trimIndent()
    )
}