package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.testing.*
import kotlin.test.Test

class SuspendedRequestRoutesTest {
    @Test
    fun `routes through with GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/suspend_request_routes").apply {
            assertThat(response.content).isEqualTo("Suspend GET.")
        }
    }
}
