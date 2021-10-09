package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.HttpMethod.Companion.Delete
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Head
import io.ktor.http.HttpMethod.Companion.Options
import io.ktor.http.HttpMethod.Companion.Patch
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.http.HttpMethod.Companion.Put
import io.ktor.server.testing.*
import kotlin.test.Test

class BoringRequestRoutesTest {
    @Test
    fun `routes through with GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/boring_request_routes").apply {
            assertThat(response.content).isEqualTo("Boring GET.")
        }
    }

    @Test
    fun `routes through with POST`() = withConfiguredTestApplication {
        handleRequest(Post, "/boring_request_routes").apply {
            assertThat(response.content).isEqualTo("Boring POST.")
        }
    }

    @Test
    fun `routes through with PUT`() = withConfiguredTestApplication {
        handleRequest(Put, "/boring_request_routes").apply {
            assertThat(response.content).isEqualTo("Boring PUT.")
        }
    }

    @Test
    fun `routes through with PATCH`() = withConfiguredTestApplication {
        handleRequest(Patch, "/boring_request_routes").apply {
            assertThat(response.content).isEqualTo("Boring PATCH.")
        }
    }

    @Test
    fun `routes through with DELETE`() = withConfiguredTestApplication {
        handleRequest(Delete, "/boring_request_routes").apply {
            assertThat(response.content).isEqualTo("Boring DELETE.")
        }
    }

    @Test
    fun `routes through with HEAD`() = withConfiguredTestApplication {
        handleRequest(Head, "/boring_request_routes").apply {
            assertThat(response.content).isEqualTo("Boring HEAD.")
        }
    }

    @Test
    fun `routes through with OPTIONS`() = withConfiguredTestApplication {
        handleRequest(Options, "/boring_request_routes").apply {
            assertThat(response.content).isEqualTo("Boring OPTIONS.")
        }
    }
}