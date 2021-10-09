package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Delete
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Head
import io.ktor.http.HttpMethod.Companion.Options
import io.ktor.http.HttpMethod.Companion.Patch
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.http.HttpMethod.Companion.Put
import io.ktor.server.testing.*
import kotlin.test.Test

class AmendedRequestRoutesTest {
    @Test
    fun `routes through with GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/amended_routes_segment/amended_routes").apply {
            assertThat(response.content).isEqualTo("Amended GET.")
        }
    }

    @Test
    fun `routes through with POST`() = withConfiguredTestApplication {
        handleRequest(Post, "/amended_routes_segment/amended_routes").apply {
            assertThat(response.content).isEqualTo("Amended POST.")
        }
    }

    @Test
    fun `routes through with PUT`() = withConfiguredTestApplication {
        handleRequest(Put, "/amended_routes_segment/amended_routes").apply {
            assertThat(response.content).isEqualTo("Amended PUT.")
        }
    }

    @Test
    fun `routes through with PATCH`() = withConfiguredTestApplication {
        handleRequest(Patch, "/amended_routes_segment/amended_routes").apply {
            assertThat(response.content).isEqualTo("Amended PATCH.")
        }
    }

    @Test
    fun `routes through with DELETE`() = withConfiguredTestApplication {
        handleRequest(Delete, "/amended_routes_segment/amended_routes").apply {
            assertThat(response.content).isEqualTo("Amended DELETE.")
        }
    }

    @Test
    fun `routes through with HEAD`() = withConfiguredTestApplication {
        handleRequest(Head, "/amended_routes_segment/amended_routes").apply {
            assertThat(response.content).isEqualTo("Amended HEAD.")
        }
    }

    @Test
    fun `routes through with OPTIONS`() = withConfiguredTestApplication {
        handleRequest(Options, "/amended_routes_segment/amended_routes").apply {
            assertThat(response.content).isEqualTo("Amended OPTIONS.")
        }
    }

    @Test
    fun `does not route through with original name GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/amended_routes_segment").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name POST`() = withConfiguredTestApplication {
        handleRequest(Post, "/amended_routes_segment").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name PUT`() = withConfiguredTestApplication {
        handleRequest(Put, "/amended_routes_segment").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name PATCH`() = withConfiguredTestApplication {
        handleRequest(Patch, "/amended_routes_segment").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name DELETE`() = withConfiguredTestApplication {
        handleRequest(Delete, "/amended_routes_segment").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name HEAD`() = withConfiguredTestApplication {
        handleRequest(Head, "/amended_routes_segment").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name OPTIONS`() = withConfiguredTestApplication {
        handleRequest(Options, "/amended_routes_segment").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }
}