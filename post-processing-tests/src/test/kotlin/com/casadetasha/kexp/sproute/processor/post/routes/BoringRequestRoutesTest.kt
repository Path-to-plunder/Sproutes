package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class BoringRequestRoutesTest {
    @Test
    fun `routes through with GET`() = configuredTestApplication {
        val response = client.get("/boring_request_routes")
        assertThat(response.bodyAsText()).isEqualTo("Boring GET.")
    }

    @Test
    fun `routes through with POST`() = configuredTestApplication {
        val response = client.post("/boring_request_routes")
        assertThat(response.bodyAsText()).isEqualTo("Boring POST.")
    }

    @Test
    fun `routes through with PUT`() = configuredTestApplication {
        val response = client.put("/boring_request_routes")
        assertThat(response.bodyAsText()).isEqualTo("Boring PUT.")
    }

    @Test
    fun `routes through with PATCH`() = configuredTestApplication {
        val response = client.patch("/boring_request_routes")
        assertThat(response.bodyAsText()).isEqualTo("Boring PATCH.")
    }

    @Test
    fun `routes through with DELETE`() = configuredTestApplication {
        val response = client.delete("/boring_request_routes")
        assertThat(response.bodyAsText()).isEqualTo("Boring DELETE.")
    }

    @Test
    fun `routes through with HEAD`() = configuredTestApplication {
        val response = client.head("/boring_request_routes")
        assertThat(response.bodyAsText()).isEqualTo("Boring HEAD.")
    }

    @Test
    fun `routes through with OPTIONS`() = configuredTestApplication {
        val response = client.options("/boring_request_routes")
        assertThat(response.bodyAsText()).isEqualTo("Boring OPTIONS.")
    }
}