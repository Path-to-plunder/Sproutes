package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ParameterizedRoutesTest {
    @Test
    fun `method is provided ApplicationCall`() = configuredTestApplication {
        val response = client.get("/routes/parameterized/call")
        assertThat(response.bodyAsText()).isEqualTo("Responded from method call param")
    }

    @Test
    fun `method is provided Application`() = configuredTestApplication {
        val response = client.get("/routes/parameterized/application")
        assertThat(response.bodyAsText()).isEqualTo("My root path is ")
    }

    @Test
    fun `class is provided ApplicationCall`() = configuredTestApplication {
        val response = client.get("routes/parameterized/class_call")
        assertThat(response.bodyAsText()).isEqualTo("Responded from class call param")
    }

    @Test
    fun `class is provided application`() = configuredTestApplication {
        val response = client.get("routes/parameterized/class_application")
        assertThat(response.bodyAsText()).isEqualTo("My root path is ")
    }
}
