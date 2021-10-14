package com.casadetasha.kexp.sproute.processor.post.routes.authenticated

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Get
import com.casadetasha.kexp.sproute.annotations.Sproute
import com.casadetasha.kexp.sproute.annotations.Unauthenticated

@Sproute("/authenticated_class")
@Authenticated
internal class AuthenticatedClassRoutes {
    @Get
    fun authenticatedClassGet() = "Authenticated class route GET."

    @Get("/override_auth")
    @Authenticated("named-auth")
    fun authenticatedClassOverrideAuthRequestGet() = "Authenticated class override auth route GET."

    @Get("/override_unauthenticated")
    @Unauthenticated
    fun authenticatedClassOverrideUnauthenticatedRequestGet() = "Authenticated class override unauthenticated route GET."
}
