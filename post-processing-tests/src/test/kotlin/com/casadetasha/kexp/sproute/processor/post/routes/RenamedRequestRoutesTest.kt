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

class RenamedRequestRoutesTest {
    @Test
    fun `routes through with GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/renamed_routes").apply {
            assertThat(response.content).isEqualTo("Renamed GET.")
        }
    }

    @Test
    fun `routes through with POST`() = withConfiguredTestApplication {
        handleRequest(Post, "/renamed_routes").apply {
            assertThat(response.content).isEqualTo("Renamed POST.")
        }
    }

    @Test
    fun `routes through with PUT`() = withConfiguredTestApplication {
        handleRequest(Put, "/renamed_routes").apply {
            assertThat(response.content).isEqualTo("Renamed PUT.")
        }
    }

    @Test
    fun `routes through with PATCH`() = withConfiguredTestApplication {
        handleRequest(Patch, "/renamed_routes").apply {
            assertThat(response.content).isEqualTo("Renamed PATCH.")
        }
    }

    @Test
    fun `routes through with DELETE`() = withConfiguredTestApplication {
        handleRequest(Delete, "/renamed_routes").apply {
            assertThat(response.content).isEqualTo("Renamed DELETE.")
        }
    }

    @Test
    fun `routes through with HEAD`() = withConfiguredTestApplication {
        handleRequest(Head, "/renamed_routes").apply {
            assertThat(response.content).isEqualTo("Renamed HEAD.")
        }
    }

    @Test
    fun `routes through with OPTIONS`() = withConfiguredTestApplication {
        handleRequest(Options, "/renamed_routes").apply {
            assertThat(response.content).isEqualTo("Renamed OPTIONS.")
        }
    }

    @Test
    fun `does not route through with original name GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/original_name_routes").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name POST`() = withConfiguredTestApplication {
        handleRequest(Post, "/original_name_routes").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name PUT`() = withConfiguredTestApplication {
        handleRequest(Put, "/original_name_routes").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name PATCH`() = withConfiguredTestApplication {
        handleRequest(Patch, "/original_name_routes").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name DELETE`() = withConfiguredTestApplication {
        handleRequest(Delete, "/original_name_routes").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name HEAD`() = withConfiguredTestApplication {
        handleRequest(Head, "/original_name_routes").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `does not route through with original name OPTIONS`() = withConfiguredTestApplication {
        handleRequest(Options, "/original_name_routes").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }
}