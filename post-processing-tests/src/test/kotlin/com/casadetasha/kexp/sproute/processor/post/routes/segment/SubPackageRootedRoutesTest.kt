package com.casadetasha.kexp.sproute.processor.post.routes.segment

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class SubPackageRootedRoutesTest {

    @Test
    fun `routes through to boring get`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/root/routes/segment/routes").apply {
            assertThat(response.content).isEqualTo("Sub package route boring route GET.")
        }
    }

    @Test
    fun `routes through to amended segment get`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/root/routes/segment/routes/amended_segment").apply {
            assertThat(response.content).isEqualTo("Sub package route slightly less boring amended route GET!")
        }
    }
}