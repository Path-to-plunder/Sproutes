package com.casadetasha.kexp.sproute.processor.post.routes.segment

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class BoringRootedRoutesTest {

    @Test
    fun `routes through to boring get`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/root/routes").apply {
            assertThat(response.content).isEqualTo("Boring rooted route boring route GET.")
        }
    }

    @Test
    fun `routes through to amended segment get`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/root/routes/amended_segment").apply {
            assertThat(response.content).isEqualTo("Boring rooted route slightly less boring amended route GET!")
        }
    }

    @Test
    fun `routes through to renamed segment get`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/root/renamed_routes").apply {
            assertThat(response.content).isEqualTo(
                "Boring rooted route slightly less boring replaced route GET, estoy ahogado en un baaaaaaar!")
        }
    }
}
