package com.casadetasha.kexp.sproute.processor.post.routes.authenticated

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute

@Sproute("/authenticated_request")
internal class AuthenticatedRequestRoutes {
    @Get("/required_unnamed")
    @Authenticated
    fun authenticatedRequestGet() = "Unnamed authenticated request route GET."

    @Get("/required_named")
    @Authenticated("named-auth")
    fun namedAuthenticatedRequestGet() = "Named authenticated request route GET."

    @Get("/required_multi_named")
    @Authenticated("named-auth", "secondary-named-auth")
    fun multiNamedAuthenticatedRequestGet() = "Multi-named authenticated request route GET."

    @Get("/optional_unnamed")
    @Authenticated(optional = true)
    fun optionalAuthenticatedRequestGet() = "Optional unnamed authenticated request route GET."

    @Get("/optional_named")
    @Authenticated("named-auth", optional = true)
    fun optionalNamedAuthenticatedRequestGet() = "Optional named authenticated request route GET."
}
