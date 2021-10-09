package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.testing.*
import kotlin.test.Test

class RouteExtensionFunctionRoutesTests {
    @Test
    fun `routes through with GET`() = withConfiguredTestApplication {
        handleRequest(Get, "/route_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("Route Extension function GOTTED!")
        }
    }

    @Test
    fun `routes through with POST`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Post, "/route_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("Route Extension function POSTED!")
        }
    }

    @Test
    fun `routes through with PUT`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Put, "/route_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("Route Extension function PUTTED!")
        }
    }

    @Test
    fun `routes through with PATCH`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Patch, "/route_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("Route Extension function PATCHED!")
        }
    }

    @Test
    fun `routes through with DELETE`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Delete, "/route_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("Route Extension function DELETED!")
        }
    }

    @Test
    fun `routes through with HEAD`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Head, "/route_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("Route Extension function HEADED!")
        }
    }

    @Test
    fun `routes through with OPTIONS`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Options, "/route_extension_function_routes").apply {
            assertThat(response.content).isEqualTo("Route Extension function OPTIONED!")
        }
    }
}
