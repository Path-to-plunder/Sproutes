package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class RouteExtensionFunctionRoutesTests {
    @Test
    fun `routes through with GET`() = configuredTestApplication {
        val response = client.get("/route_extension_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Route Extension function GOTTED!")
    }

    @Test
    fun `routes through with POST`() = configuredTestApplication {
        val response = client.post("/route_extension_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Route Extension function POSTED!")
    }

    @Test
    fun `routes through with PUT`() = configuredTestApplication {
        val response = client.put("/route_extension_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Route Extension function PUTTED!")
    }

    @Test
    fun `routes through with PATCH`() = configuredTestApplication {
        val response = client.patch("/route_extension_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Route Extension function PATCHED!")
    }

    @Test
    fun `routes through with DELETE`() = configuredTestApplication {
        val response = client.delete("/route_extension_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Route Extension function DELETED!")
    }

    @Test
    fun `routes through with HEAD`() = configuredTestApplication {
        val response = client.head("/route_extension_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Route Extension function HEADED!")
    }

    @Test
    fun `routes through with OPTIONS`() = configuredTestApplication {
        val response = client.options("/route_extension_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Route Extension function OPTIONED!")
    }
}
