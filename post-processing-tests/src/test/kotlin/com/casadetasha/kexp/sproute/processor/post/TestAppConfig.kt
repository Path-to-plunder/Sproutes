package com.casadetasha.kexp.sproute.processor.post

import com.casadetasha.kexp.sproute.configureSproutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.server.testing.*
import java.util.*

internal fun withConfiguredTestApplication(moduleFunction: TestApplicationEngine.() -> Unit) {
    return withTestApplication(
        {
            configureAuth()
            configureSproutes()
        },
        moduleFunction)
}

fun Application.configureAuth() {
    authentication {
        basic {
            realm = "Access to authenticated tests"
            addBasicValidation("username", "password")
        }

        basic("named-auth") {
            realm = "Access to authenticated tests"
            addBasicValidation("namedUsername", "namedPassword")
        }

        basic("secondary-named-auth") {
            realm = "Access to authenticated tests"
            addBasicValidation("secondaryNamedUsername", "secondaryNamedPassword")
        }
    }
}

private fun BasicAuthenticationProvider.Configuration.addBasicValidation(username: String, password: String) {
    validate { credentials ->
        if (credentials.name == username && credentials.password == password) {
            UserIdPrincipal(credentials.name)
        } else {
            null
        }
    }

}

internal fun TestApplicationRequest.addBasicAuthHeader(username: String, password: String) {
    val credentials = "${username}:$password".toBase64()
    addHeader("Authorization", "Basic $credentials")
}

private fun String.toBase64(): String {
    return Base64.getEncoder().encodeToString(toByteArray())
}
