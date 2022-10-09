package com.casadetasha.kexp.sproute.processor.source

import com.tschuchort.compiletesting.SourceFile

object AuthenticationSourceFiles {
    internal val inlineAuthRouteSource = SourceFile.kotlin(
        "BoringRouteSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Authenticated
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute

            @Sproute("/route")
            @Authenticated
            class Route {
                @Get("/get-a-route")
                fun get() = "get me"
            }
        """.trimIndent()
    )

    internal val inheritedAuthRouteSource = SourceFile.kotlin(
        "BoringRouteSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Authenticated
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import com.casadetasha.kexp.sproute.annotations.Unauthenticated

            @Sproute("/authenticated_sproute_root")
            @Authenticated
            interface AuthenticatedSprouteRoot

            @Get("/function")
            @Sproute(sprouteRoot = AuthenticatedSprouteRoot::class)
            fun authenticatedSprouteRootOverrideFunctionGet() = "Authenticated sproute route function GET."

            @Get("/function/override_auth")
            @Authenticated("named-auth")
            @Sproute(sprouteRoot = AuthenticatedSprouteRoot::class)
            fun authenticatedSprouteRootOverrideAuthRequestOverrideAuthRequestFunction() = "Authenticated sproute override auth route function GET."

            @Get("/function/override_unauthenticated")
            @Unauthenticated
            @Sproute(sprouteRoot = AuthenticatedSprouteRoot::class)
            fun authenticatedSprouteRootOverrideAuthRequestOverrideUnauthenticatedRequestFunction() =
                "Authenticated sproute override unauthenticated route function GET."
        """.trimIndent()
    )
}
