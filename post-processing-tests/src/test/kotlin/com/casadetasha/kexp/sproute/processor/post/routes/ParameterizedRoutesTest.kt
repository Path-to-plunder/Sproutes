package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ParameterizedRoutesTest {
    @Test
    fun `method is provided ApplicationCall`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/routes/parameterized/call").apply {
            assertThat(response.content).isEqualTo("Responded from method call param")
        }
    }

    @Test
    fun `method is provided Application`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "/routes/parameterized/application").apply {
            assertThat(response.content).isEqualTo("My root path is ")
        }
    }

    @Test
    fun `class is provided ApplicationCall`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "routes/parameterized/class_call").apply {
            assertThat(response.content).isEqualTo("Responded from class call param")
        }
    }

    @Test
    fun `class is provided application`() = withConfiguredTestApplication {
        handleRequest(HttpMethod.Get, "routes/parameterized/class_application").apply {
            assertThat(response.content).isEqualTo("My root path is ")
        }
    }
}
