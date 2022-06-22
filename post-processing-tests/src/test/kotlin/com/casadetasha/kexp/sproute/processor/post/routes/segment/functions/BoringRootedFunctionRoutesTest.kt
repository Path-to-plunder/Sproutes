package com.casadetasha.kexp.sproute.processor.post.routes.segment.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class BoringRootedFunctionRoutesTest {
    @Test
    fun `routes through to boring get`() = configuredTestApplication {
        val response = client.get("/root/request_routes")
        assertThat(response.bodyAsText()).isEqualTo("GOT from a boring function with route in request.")
    }

    @Test
    fun `routes through to amended segment get`() = configuredTestApplication {
        val response = client.get("/root/sproute_routes")
        assertThat(response.bodyAsText()).isEqualTo("GOT from a boring function with route in sproute.")
    }

    @Test
    fun `routes through to renamed segment get`() = configuredTestApplication {
        val response = client.get("/root/sproute/request_routes")
        assertThat(response.bodyAsText()).isEqualTo(
                "GOT from a boring function with route in sproute and request.")
    }
}