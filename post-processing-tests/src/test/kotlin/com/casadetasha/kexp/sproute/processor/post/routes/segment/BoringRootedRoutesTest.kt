package com.casadetasha.kexp.sproute.processor.post.routes.segment

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class BoringRootedRoutesTest {

    @Test
    fun `routes through to boring get`() = configuredTestApplication {
        val response = client.get("/root/routes")
        assertThat(response.bodyAsText()).isEqualTo("Boring rooted route boring route GET.")
    }

    @Test
    fun `routes through to amended segment get`() = configuredTestApplication {
        val response = client.get("/root/routes/amended_segment")
        assertThat(response.bodyAsText()).isEqualTo("Boring rooted route slightly less boring amended route GET!")
    }
}
