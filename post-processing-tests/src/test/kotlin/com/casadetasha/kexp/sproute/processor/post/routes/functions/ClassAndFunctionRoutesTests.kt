package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.HttpMethod.Companion.Delete
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Head
import io.ktor.http.HttpMethod.Companion.Options
import io.ktor.http.HttpMethod.Companion.Patch
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.http.HttpMethod.Companion.Put
import io.ktor.server.testing.*
import kotlin.test.Test

class ClassAndFunctionRoutesTests {
    class InsideClassTests {
        @Test
        fun `routes through with inside class GET`() = withConfiguredTestApplication {
            handleRequest(Get, "/class_and_function_routes/inside_function").apply {
                assertThat(response.content).isEqualTo("Inside class GOTTED!")
            }
        }

        @Test
        fun `routes through with inside class POST`() = withConfiguredTestApplication {
            handleRequest(Post, "/class_and_function_routes/inside_function").apply {
                assertThat(response.content).isEqualTo("Inside class POSTED!")
            }
        }

        @Test
        fun `routes through with inside class PUT`() = withConfiguredTestApplication {
            handleRequest(Put, "/class_and_function_routes/inside_function").apply {
                assertThat(response.content).isEqualTo("Inside class PUTTED!")
            }
        }

        @Test
        fun `routes through with inside class PATCH`() = withConfiguredTestApplication {
            handleRequest(Patch, "/class_and_function_routes/inside_function").apply {
                assertThat(response.content).isEqualTo("Inside class PATCHED!")
            }
        }

        @Test
        fun `routes through with inside class DELETE`() = withConfiguredTestApplication {
            handleRequest(Delete, "/class_and_function_routes/inside_function").apply {
                assertThat(response.content).isEqualTo("Inside class DELETED!")
            }
        }

        @Test
        fun `routes through with inside class HEAD`() = withConfiguredTestApplication {
            handleRequest(Head, "/class_and_function_routes/inside_function").apply {
                assertThat(response.content).isEqualTo("Inside class HEADED!")
            }
        }

        @Test
        fun `routes through with inside class OPTIONS`() = withConfiguredTestApplication {
            handleRequest(Options, "/class_and_function_routes/inside_function").apply {
                assertThat(response.content).isEqualTo("Inside class OPTIONED!")
            }
        }
    }

    class OutsideClassTests {
        @Test
        fun `routes through with outside class GET`() = withConfiguredTestApplication {
            handleRequest(Get, "/class_and_function_routes/outside_function").apply {
                assertThat(response.content).isEqualTo("Outside class GOTTED!")
            }
        }

        @Test
        fun `routes through with outside class POST`() = withConfiguredTestApplication {
            handleRequest(Post, "/class_and_function_routes/outside_function").apply {
                assertThat(response.content).isEqualTo("Outside class POSTED!")
            }
        }

        @Test
        fun `routes through with outside class PUT`() = withConfiguredTestApplication {
            handleRequest(Put, "/class_and_function_routes/outside_function").apply {
                assertThat(response.content).isEqualTo("Outside class PUTTED!")
            }
        }

        @Test
        fun `routes through with outside class PATCH`() = withConfiguredTestApplication {
            handleRequest(Patch, "/class_and_function_routes/outside_function").apply {
                assertThat(response.content).isEqualTo("Outside class PATCHED!")
            }
        }

        @Test
        fun `routes through with outside class DELETE`() = withConfiguredTestApplication {
            handleRequest(Delete, "/class_and_function_routes/outside_function").apply {
                assertThat(response.content).isEqualTo("Outside class DELETED!")
            }
        }

        @Test
        fun `routes through with outside class HEAD`() = withConfiguredTestApplication {
            handleRequest(Head, "/class_and_function_routes/outside_function").apply {
                assertThat(response.content).isEqualTo("Outside class HEADED!")
            }
        }

        @Test
        fun `routes through with outside class OPTIONS`() = withConfiguredTestApplication {
            handleRequest(Options, "/class_and_function_routes/outside_function").apply {
                assertThat(response.content).isEqualTo("Outside class OPTIONED!")
            }
        }
    }
}