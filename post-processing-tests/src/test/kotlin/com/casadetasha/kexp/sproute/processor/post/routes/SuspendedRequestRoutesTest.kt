package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class SuspendedRequestRoutesTest {
    @Test
    fun `routes through with GET`() = configuredTestApplication {
        val response = client.get("/suspend_request_routes")
        assertThat(response.bodyAsText()).isEqualTo("Suspend GET.")
    }
}
