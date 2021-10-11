package com.casadetasha.kexp.sproute.processor.post.routes.authenticated

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute

@Sproute("/authenticated")
internal class AuthenticatedRoutes {
    @Get("/required-unnamed")
    @Authenticated
    fun authenticatedGet() = "Unnamed authenticated route GET."

    @Get("/required-named")
    @Authenticated("named-auth")
    fun namedAuthenticatedGet() = "Named authenticated route GET."

    @Get("/required-multi-named")
    @Authenticated("named-auth", "secondary-named-auth")
    fun multiNamedAuthenticatedGet() = "Multi-named authenticated route GET."
}
