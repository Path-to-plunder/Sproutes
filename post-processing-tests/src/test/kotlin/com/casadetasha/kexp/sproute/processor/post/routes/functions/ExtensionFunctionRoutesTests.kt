package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.testing.*
import kotlin.test.Test

class ExtensionFunctionRoutesTests {
    @Test
    fun `routes through from ApplicationCall extension method`() = withConfiguredTestApplication {
        handleRequest(Get, "/extension_function_routes/application_call").apply {
            assertThat(response.content).isEqualTo("This is a responded through the application call I'm extending!")
        }
    }

    @Test
    fun `routes through from Route extension method`() = withConfiguredTestApplication {
        handleRequest(Get, "/extension_function_routes/route").apply {
            assertThat(response.content).isEqualTo("My route is /extension_function_routes/route")
        }
    }
}
