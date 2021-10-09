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

class SuspendRequestRoutesTest {
    @Test
    fun `routes through with GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/suspend_request_routes").apply {
            assertThat(response.content).isEqualTo("Suspend GET.")
        }
    }

    @Test
    fun `routes through with POST`() = withConfiguredTestApplication {
        handleRequest(Post, "/suspend_request_routes").apply {
            assertThat(response.content).isEqualTo("Suspend POST.")
        }
    }

    @Test
    fun `routes through with PUT`() = withConfiguredTestApplication {
        handleRequest(Put, "/suspend_request_routes").apply {
            assertThat(response.content).isEqualTo("Suspend PUT.")
        }
    }

    @Test
    fun `routes through with PATCH`() = withConfiguredTestApplication {
        handleRequest(Patch, "/suspend_request_routes").apply {
            assertThat(response.content).isEqualTo("Suspend PATCH.")
        }
    }

    @Test
    fun `routes through with DELETE`() = withConfiguredTestApplication {
        handleRequest(Delete, "/suspend_request_routes").apply {
            assertThat(response.content).isEqualTo("Suspend DELETE.")
        }
    }

    @Test
    fun `routes through with HEAD`() = withConfiguredTestApplication {
        handleRequest(Head, "/suspend_request_routes").apply {
            assertThat(response.content).isEqualTo("Suspend HEAD.")
        }
    }

    @Test
    fun `routes through with OPTIONS`() = withConfiguredTestApplication {
        handleRequest(Options, "/suspend_request_routes").apply {
            assertThat(response.content).isEqualTo("Suspend OPTIONS.")
        }
    }
}