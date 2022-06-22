package com.casadetasha.kexp.sproute.processor.post.routes.authenticated

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.addBasicAuthHeader
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import kotlin.test.Test

class AuthenticatedRequestRoutesTest {

    companion object {
        const val BASE_URL: String = "authenticated_request"
    }
    @Test
    fun `authenticated get with invalid auth returns status Unauthorized (401)`() = configuredTestApplication {
        val response = client.get("$BASE_URL/required_unnamed")
        assertThat(response.status).isEqualTo(Unauthorized)
    }

    @Test
    fun `authenticated get with valid auth routes through`() = configuredTestApplication {
        val response = client.get("$BASE_URL/required_unnamed") {
            addBasicAuthHeader("username", "password")
        }
        assertThat(response.status).isEqualTo(OK)
        assertThat(response.bodyAsText()).isEqualTo("Unnamed authenticated request route GET.")
    }

    @Test
    fun `named authenticated get with invalid auth returns status Unauthorized (401)`() =
        configuredTestApplication {
            val response = client.get("$BASE_URL/required_named")
            assertThat(response.status).isEqualTo(Unauthorized)
        }

    @Test
    fun `named authenticated get with valid auth routes through`() = configuredTestApplication {
        val response = client.get("$BASE_URL/required_named") {
            addBasicAuthHeader("namedUsername", "namedPassword")
        }
        assertThat(response.bodyAsText()).isEqualTo("Named authenticated request route GET.")
    }

    @Test
    fun `multi_named authenticated get with invalid auth on first auth_name returns status Unauthorized (401)`() =
        configuredTestApplication {
            val response = client.get("$BASE_URL/required_multi_named")
            assertThat(response.status).isEqualTo(Unauthorized)
        }

    @Test
    fun `multi_named authenticated get with valid auth on first auth_name routes through`() =
        configuredTestApplication {
            val response = client.get("$BASE_URL/required_multi_named") {
                addBasicAuthHeader("namedUsername", "namedPassword")
            }
            assertThat(response.bodyAsText()).isEqualTo("Multi-named authenticated request route GET.")
        }

    @Test
    fun `multi_named authenticated get with valid auth on extra auth_name routes through`() =
        configuredTestApplication {
            val response = client.get("$BASE_URL/required_multi_named") {
                addBasicAuthHeader("secondaryNamedUsername", "secondaryNamedPassword")
            }
            assertThat(response.bodyAsText()).isEqualTo("Multi-named authenticated request route GET.")
        }

    @Test
    fun `optional unnamed authenticated get without auth routes through`() = configuredTestApplication {
        val response = client.get("$BASE_URL/optional_unnamed")
        assertThat(response.bodyAsText()).isEqualTo("Optional unnamed authenticated request route GET.")
    }

    @Test
    fun `optional unnamed authenticated get with valid auth routes through`() = configuredTestApplication {
        val response = client.get("$BASE_URL/optional_unnamed") {
            addBasicAuthHeader("username", "password")
        }
        assertThat(response.bodyAsText()).isEqualTo("Optional unnamed authenticated request route GET.")
    }

    @Test
    fun `optionally named authenticated get without auth routes through`() = configuredTestApplication {
        val response = client.get("$BASE_URL/optional_named")
        assertThat(response.bodyAsText()).isEqualTo("Optional named authenticated request route GET.")
    }

    @Test
    fun `optionally named authenticated get with valid auth routes through`() = configuredTestApplication {
        val response = client.get("$BASE_URL/optional_named") {
            addBasicAuthHeader("namedUsername", "namedPassword")
        }
        assertThat(response.bodyAsText()).isEqualTo("Optional named authenticated request route GET.")
    }
}
