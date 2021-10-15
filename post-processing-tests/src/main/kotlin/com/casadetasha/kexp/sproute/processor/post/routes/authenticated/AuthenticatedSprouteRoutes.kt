package com.casadetasha.kexp.sproute.processor.post.routes.authenticated

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.Unauthenticated

@Sproute("/authenticated_sproute")
@Authenticated
internal class AuthenticatedSprouteRoutes {
    @Get
    fun authenticatedSprouteGet() = "Authenticated sproute route GET."

    @Get("/override_auth")
    @Authenticated("named-auth")
    fun authenticatedClassOverrideAuthRequestGet() = "Authenticated sproute override auth route GET."

    @Get("/override_unauthenticated")
    @Unauthenticated
    fun authenticatedClassOverrideUnauthenticatedRequestGet() =
        "Authenticated sproute override unauthenticated route GET."
}
