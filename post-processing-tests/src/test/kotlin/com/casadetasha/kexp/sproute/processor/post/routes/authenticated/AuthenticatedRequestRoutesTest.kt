package com.casadetasha.kexp.sproute.processor.post.routes.authenticated

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.addBasicAuthHeader
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.testing.*
import kotlin.test.Test

class AuthenticatedRequestRoutesTest {

    companion object {
        const val BASE_URL: String = "authenticated_request"
    }
    @Test
    fun `authenticated get with invalid auth returns status Unauthorized (401)`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL/required_unnamed").apply {
            assertThat(response.status()).isEqualTo(Unauthorized)
        }
    }

    @Test
    fun `authenticated get with valid auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL/required_unnamed") {
            addBasicAuthHeader("username", "password")
        }.apply {
            assertThat(response.content).isEqualTo("Unnamed authenticated request route GET.")
        }
    }

    @Test
    fun `named authenticated get with invalid auth returns status Unauthorized (401)`() =
        withConfiguredTestApplication {
            handleRequest(HttpMethod.Get, "$BASE_URL/required_named").apply {
                assertThat(response.status()).isEqualTo(Unauthorized)
            }
        }

    @Test
    fun `named authenticated get with valid auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL/required_named") {
            addBasicAuthHeader("namedUsername", "namedPassword")
        }.apply {
            assertThat(response.content).isEqualTo("Named authenticated request route GET.")
        }
    }

    @Test
    fun `multi_named authenticated get with invalid auth on first auth_name returns status Unauthorized (401)`() =
        withConfiguredTestApplication {
            handleRequest(HttpMethod.Get, "$BASE_URL/required_multi_named").apply {
                assertThat(response.status()).isEqualTo(Unauthorized)
            }
        }

    @Test
    fun `multi_named authenticated get with valid auth on first auth_name routes through`() =
        withConfiguredTestApplication {
            handleRequest(HttpMethod.Get, "$BASE_URL/required_multi_named") {
                addBasicAuthHeader("namedUsername", "namedPassword")
            }.apply {
                assertThat(response.content).isEqualTo("Multi-named authenticated request route GET.")
            }
        }

    @Test
    fun `multi_named authenticated get with valid auth on extra auth_name routes through`() =
        withConfiguredTestApplication {
            handleRequest(HttpMethod.Get, "$BASE_URL/required_multi_named") {
                addBasicAuthHeader("secondaryNamedUsername", "secondaryNamedPassword")
            }.apply {
                assertThat(response.content).isEqualTo("Multi-named authenticated request route GET.")
            }
        }

    @Test
    fun `optional unnamed authenticated get without auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL/optional_unnamed").apply {
            assertThat(response.content).isEqualTo("Optional unnamed authenticated request route GET.")
        }
    }

    @Test
    fun `optional unnamed authenticated get with valid auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL/optional_unnamed") {
            addBasicAuthHeader("username", "password")
        }.apply {
            assertThat(response.content).isEqualTo("Optional unnamed authenticated request route GET.")
        }
    }

    @Test
    fun `optionally named authenticated get without auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL/optional_named").apply {
            assertThat(response.content).isEqualTo("Optional named authenticated request route GET.")
        }
    }

    @Test
    fun `optionally named authenticated get with valid auth routes through`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "$BASE_URL/optional_named") {
            addBasicAuthHeader("namedUsername", "namedPassword")
        }.apply {
            assertThat(response.content).isEqualTo("Optional named authenticated request route GET.")
        }
    }
}
