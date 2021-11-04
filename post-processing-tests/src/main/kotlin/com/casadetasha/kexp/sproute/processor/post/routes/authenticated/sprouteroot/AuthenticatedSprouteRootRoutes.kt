package com.casadetasha.kexp.sproute.processor.post.routes.authenticated.sprouteroot

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.post.AuthenticatedSprouteRoot

@Sproute(sprouteRoot = AuthenticatedSprouteRoot::class)
internal class AuthenticatedSprouteRootRoutes {
    @Get
    fun authenticatedSprouteRootOverrideGet() = "Authenticated sproute route GET."

    @Get("/override_auth")
    @Authenticated("named-auth")
    fun authenticatedSprouteRootOverrideAuthRequestOverrideAuthRequestGet() = "Authenticated sproute override auth route GET."

    @Get("/override_unauthenticated")
    @Unauthenticated
    fun authenticatedSprouteRootOverrideAuthRequestOverrideUnauthenticatedRequestGet() =
        "Authenticated sproute override unauthenticated route GET."
}
