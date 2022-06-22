package com.casadetasha.kexp.sproute.processor.post.routes.authenticated.sprouteroot

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.addBasicAuthHeader
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import kotlin.test.Test

class AuthenticatedSproutePackageRootRoutesTest {

    companion object {
        const val BASE_URL: String = "authenticated_sproute_root"
    }

    @Test
    fun `authenticated sproute get with invalid auth returns status Unauthorized (401)`() =
        configuredTestApplication {
            val response = client.get(BASE_URL)
            assertThat(response.status).isEqualTo(Unauthorized)
        }

    @Test
    fun `authenticated sproute get with valid auth routes through`() = configuredTestApplication {
        val response = client.get(BASE_URL) {
            addBasicAuthHeader("username", "password")
        }
        assertThat(response.bodyAsText()).isEqualTo("Authenticated sproute route GET.")
    }

    @Test
    fun `overridden authenticated get with no auth returns status Unauthorized (401)`() =
        configuredTestApplication {
            val response = client.get("$BASE_URL/override_auth")
            assertThat(response.status).isEqualTo(Unauthorized)
        }

    @Test
    fun `overridden authenticated get with valid auth for parent returns status Unauthorized (401)`() =
        configuredTestApplication {
            val response = client.get("$BASE_URL/override_auth") {
                addBasicAuthHeader("username", "password")
            }
            assertThat(response.status).isEqualTo(Unauthorized)
        }

    @Test
    fun `overridden authenticated get with valid auth routes through`() = configuredTestApplication {
        val response = client.get("$BASE_URL/override_auth") {
            addBasicAuthHeader("namedUsername", "namedPassword")
        }
        assertThat(response.bodyAsText()).isEqualTo("Authenticated sproute override auth route GET.")
    }

    @Test
    fun `overridden unauthenticated get with no auth routes through`() =
        configuredTestApplication {
            val response = client.get("$BASE_URL/override_unauthenticated")
            assertThat(response.bodyAsText()).isEqualTo("Authenticated sproute override unauthenticated route GET.")
        }
}
