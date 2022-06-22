package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class StandaloneFunctionRoutesTests {
    @Test
    fun `routes through with GET`() = configuredTestApplication {
        val response = client.get("/standalone_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Standalone function GOTTED!")
    }

    @Test
    fun `routes through with POST`() = configuredTestApplication {
        val response = client.post("/standalone_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Standalone function POSTED!")
    }

    @Test
    fun `routes through with PUT`() = configuredTestApplication {
        val response = client.put("/standalone_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Standalone function PUTTED!")
    }

    @Test
    fun `routes through with PATCH`() = configuredTestApplication {
        val response = client.patch("/standalone_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Standalone function PATCHED!")
    }

    @Test
    fun `routes through with DELETE`() = configuredTestApplication {
        val response = client.delete("/standalone_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Standalone function DELETED!")
    }

    @Test
    fun `routes through with HEAD`() = configuredTestApplication {
        val response = client.head("/standalone_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Standalone function HEADED!")
    }

    @Test
    fun `routes through with OPTIONS`() = configuredTestApplication {
        val response = client.options("/standalone_function_routes")
        assertThat(response.bodyAsText()).isEqualTo("Standalone function OPTIONED!")
    }
}
