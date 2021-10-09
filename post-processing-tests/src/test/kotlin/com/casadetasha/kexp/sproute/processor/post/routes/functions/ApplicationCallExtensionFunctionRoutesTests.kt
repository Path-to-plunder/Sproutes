package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.testing.*
import kotlin.test.Test

class ApplicationCallExtensionFunctionRoutesTests {
    @Test
    fun `routes through with GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/application_call_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("ApplicationCall Extension function GOTTED!")
        }
    }

    @Test
    fun `routes through with POST`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Post, "/application_call_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("ApplicationCall Extension function POSTED!")
        }
    }

    @Test
    fun `routes through with PUT`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Put, "/application_call_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("ApplicationCall Extension function PUTTED!")
        }
    }

    @Test
    fun `routes through with PATCH`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Patch, "/application_call_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("ApplicationCall Extension function PATCHED!")
        }
    }

    @Test
    fun `routes through with DELETE`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Delete, "/application_call_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("ApplicationCall Extension function DELETED!")
        }
    }

    @Test
    fun `routes through with HEAD`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Head, "/application_call_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("ApplicationCall Extension function HEADED!")
        }
    }

    @Test
    fun `routes through with OPTIONS`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Options, "/application_call_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("ApplicationCall Extension function OPTIONED!")
        }
    }
}
