package com.casadetasha.kexp.sproute.processor.post

import com.casadetasha.kexp.sproute.configureSproutes
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import java.util.*

internal fun configuredTestApplication(moduleFunction: suspend ApplicationTestBuilder.() -> Unit) =
    testApplication {
        application {
            configureAuth()
            configureSproutes()
        }
        moduleFunction()
    }

fun Application.configureAuth() {
    authentication {
        basic {
            realm = "Access to authenticated tests"
            addBasicValidation("username", "password")
        }

        basic("named-auth") {
            realm = "Access to named authenticated tests"
            addBasicValidation("namedUsername", "namedPassword")
        }

        basic("secondary-named-auth") {
            realm = "Access to secondary named authenticated tests"
            addBasicValidation("secondaryNamedUsername", "secondaryNamedPassword")
        }
    }
}

private fun BasicAuthenticationProvider.Config.addBasicValidation(username: String, password: String) {
    validate { credentials ->
        if (credentials.name == username && credentials.password == password) {
            UserIdPrincipal(credentials.name)
        } else {
            null
        }
    }
}

internal fun HttpRequestBuilder.addBasicAuthHeader(username: String, password: String) {
    val credentials = "${username}:$password".toBase64()
    header("Authorization", "Basic $credentials")
}

private fun String.toBase64(): String {
    return Base64.getEncoder().encodeToString(toByteArray())
}
