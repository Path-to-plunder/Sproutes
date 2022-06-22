package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class ExtensionFunctionRoutesTests {
    @Test
    fun `routes through from ApplicationCall extension method`() = configuredTestApplication {
        val response = client.get("/extension_function_routes/application_call")
        assertThat(response.bodyAsText()).isEqualTo("This is a responded through the application call I'm extending!")
    }

    @Test
    fun `routes through from Route extension method`() = configuredTestApplication {
        val response = client.get("/extension_function_routes/route")
        assertThat(response.bodyAsText()).isEqualTo("My route is /extension_function_routes/route")
    }
}
