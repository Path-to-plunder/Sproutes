package com.casadetasha.kexp.sproute.processor.post.routes.authenticated.sprouteroot

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.addBasicAuthHeader
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.testing.*
import kotlin.test.Test

class AuthenticatedSprouteRootRoutesTest {

    companion object {
        const val BASE_URL: String = "authenticated_sproute_root"
    }

    @Test
    fun `authenticated sproute get with invalid auth returns status Unauthorized (401)`() =
        withConfiguredTestApplication {
            handleRequest(HttpMethod.Get, "$BASE_URL").apply {
                assertThat(response.status()).isEqualTo(Unauthorized)
            }
        }

    @Test
    fun `authenticated sproute get with valid auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL") {
            addBasicAuthHeader("username", "password")
        }.apply {
            assertThat(response.content).isEqualTo("Authenticated sproute route GET.")
        }
    }

    @Test
    fun `overridden authenticated get with no auth returns status Unauthorized (401)`() =
        withConfiguredTestApplication {
            handleRequest(HttpMethod.Get, "$BASE_URL/override_auth").apply {
                assertThat(response.status()).isEqualTo(Unauthorized)
            }
        }

    @Test
    fun `overridden authenticated get with valid auth for parent returns status Unauthorized (401)`() =
        withConfiguredTestApplication {
            handleRequest(HttpMethod.Get, "$BASE_URL/override_auth") {
                addBasicAuthHeader("username", "password")
            }.apply {
                assertThat(response.status()).isEqualTo(Unauthorized)
            }
        }

    @Test
    fun `overridden authenticated get with valid auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL/override_auth") {
            addBasicAuthHeader("namedUsername", "namedPassword")
        }.apply {
            assertThat(response.content).isEqualTo("Authenticated sproute override auth route GET.")
        }
    }

    @Test
    fun `overridden unauthenticated get with no auth routes through`() =
        withConfiguredTestApplication {
            handleRequest(HttpMethod.Get, "$BASE_URL/override_unauthenticated").apply {
                assertThat(response.content).isEqualTo("Authenticated sproute override unauthenticated route GET.")
            }
        }
}
