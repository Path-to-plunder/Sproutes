package com.casadetasha.kexp.sproute.processor.post.routes.segment

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class SubPackageRootedRoutesTest {

    @Test
    fun `routes through to boring get`() = configuredTestApplication {
        val response = client.get("/root/routes/segment/routes")
        assertThat(response.bodyAsText()).isEqualTo("Sub package route boring route GET.")
    }

    @Test
    fun `routes through to amended segment get`() = configuredTestApplication {
        val response = client.get("/root/routes/segment/routes/amended_segment")
        assertThat(response.bodyAsText()).isEqualTo("Sub package route slightly less boring amended route GET!")
    }
}