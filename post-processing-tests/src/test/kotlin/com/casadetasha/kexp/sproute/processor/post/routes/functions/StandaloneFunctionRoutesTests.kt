package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.testing.*
import kotlin.test.Test

class StandaloneFunctionRoutesTests {
    @Test
    fun `routes through with GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/standalone_function_routes").apply {
            assertThat(response.content).isEqualTo("Standalone function GOTTED!")
        }
    }

    @Test
    fun `routes through with POST`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Post, "/standalone_function_routes").apply {
            assertThat(response.content).isEqualTo("Standalone function POSTED!")
        }
    }

    @Test
    fun `routes through with PUT`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Put, "/standalone_function_routes").apply {
            assertThat(response.content).isEqualTo("Standalone function PUTTED!")
        }
    }

    @Test
    fun `routes through with PATCH`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Patch, "/standalone_function_routes").apply {
            assertThat(response.content).isEqualTo("Standalone function PATCHED!")
        }
    }

    @Test
    fun `routes through with DELETE`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Delete, "/standalone_function_routes").apply {
            assertThat(response.content).isEqualTo("Standalone function DELETED!")
        }
    }

    @Test
    fun `routes through with HEAD`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Head, "/standalone_function_routes").apply {
            assertThat(response.content).isEqualTo("Standalone function HEADED!")
        }
    }

    @Test
    fun `routes through with OPTIONS`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Options, "/standalone_function_routes").apply {
            assertThat(response.content).isEqualTo("Standalone function OPTIONED!")
        }
    }
}
