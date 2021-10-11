package com.casadetasha.kexp.sproute.processor.post.routes.segment.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class BoringRootedRoutesTest {
    @Test
    fun `routes through to boring get`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/root/request_routes").apply {
            assertThat(response.content).isEqualTo("GOT from a boring function with route in request.")
        }
    }

    @Test
    fun `routes through to amended segment get`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/root/sproute_routes").apply {
            assertThat(response.content).isEqualTo("GOT from a boring function with route in sproute.")
        }
    }

    @Test
    fun `routes through to renamed segment get`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/root/sproute/request_routes").apply {
            assertThat(response.content).isEqualTo(
                "GOT from a boring function with route in sproute and request.")
        }
    }
}