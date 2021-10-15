package com.casadetasha.kexp.sproute.processor.post.routes.authenticated.sprouteroot

import com.casadetasha.kexp.sproute.annotations.*
import com.casadetasha.kexp.sproute.processor.post.AuthenticatedSprouteRoot

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
