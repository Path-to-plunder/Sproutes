package com.casadetasha.kexp.sproute.processor.post.routes.segment.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class SubPackageRootedRoutesTest {

    @Test
    fun `routes through to amended segment get`() = configuredTestApplication {
        val response = client.get("/root/routes/segment/functions/sproute_routes")
        assertThat(response.bodyAsText()).isEqualTo("GOT from a sub package function with route in sproute.")
    }

    @Test
    fun `routes through to renamed segment get`() = configuredTestApplication {
        val response = client.get("/root/routes/segment/functions/sproute/request_routes")
        assertThat(response.bodyAsText()).isEqualTo(
                "GOT from a sub package function with route in sproute and request.")
    }
}
