package com.casadetasha.kexp.sproute.processor.post.routes.authenticated

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.addBasicAuthHeader
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.testing.*
import kotlin.test.Test

class AuthenticatedRoutesTest {
    @Test
    fun `authenticated get with invalid auth returns status Unauthorized (401)`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/authenticated/required-unnamed").apply {
            assertThat(response.status()).isEqualTo(Unauthorized)
        }
    }

    @Test
    fun `authenticated get with valid auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/authenticated/required-unnamed"){
            addBasicAuthHeader("username", "password")
        }.apply {
            assertThat(response.content).isEqualTo("Unnamed authenticated route GET.")
        }
    }

    @Test
    fun `named authenticated get with invalid auth returns status Unauthorized (401)`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/authenticated/required-named").apply {
            assertThat(response.status()).isEqualTo(Unauthorized)
        }
    }

    @Test
    fun `named authenticated get with valid auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/authenticated/required-named"){
            addBasicAuthHeader("namedUsername", "namedPassword")
        }.apply {
            assertThat(response.content).isEqualTo("Named authenticated route GET.")
        }
    }

    @Test
    fun `multi-named authenticated get with invalid auth on first auth-name returns status Unauthorized (401)`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/authenticated/required-multi-named").apply {
            assertThat(response.status()).isEqualTo(Unauthorized)
        }
    }

    @Test
    fun `multi-named authenticated get with valid auth on first auth-name routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/authenticated/required-multi-named"){
            addBasicAuthHeader("namedUsername", "namedPassword")
        }.apply {
            assertThat(response.content).isEqualTo("Multi-named authenticated route GET.")
        }
    }

    @Test
    fun `multi-named authenticated get with valid auth on extra auth-name routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/authenticated/required-multi-named"){
            addBasicAuthHeader("secondaryNamedUsername", "secondaryNamedPassword")
        }.apply {
            assertThat(response.content).isEqualTo("Multi-named authenticated route GET.")
        }
    }
}
