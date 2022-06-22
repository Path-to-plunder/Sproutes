package com.casadetasha.kexp.sproute.processor.post.routes.functions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class ClassAndFunctionRoutesTests {
    class InsideClassTests {
        @Test
        fun `routes through with inside class GET`() = configuredTestApplication {
            val response = client.get("/class_and_function_routes/inside_function")
            assertThat(response.bodyAsText()).isEqualTo("Inside class GOTTED!")
        }

        @Test
        fun `routes through with inside class POST`() = configuredTestApplication {
            val response = client.post("/class_and_function_routes/inside_function")
            assertThat(response.bodyAsText()).isEqualTo("Inside class POSTED!")
        }

        @Test
        fun `routes through with inside class PUT`() = configuredTestApplication {
            val response = client.put("/class_and_function_routes/inside_function")
            assertThat(response.bodyAsText()).isEqualTo("Inside class PUTTED!")
        }

        @Test
        fun `routes through with inside class PATCH`() = configuredTestApplication {
            val response = client.patch("/class_and_function_routes/inside_function")
            assertThat(response.bodyAsText()).isEqualTo("Inside class PATCHED!")
        }

        @Test
        fun `routes through with inside class DELETE`() = configuredTestApplication {
            val response = client.delete("/class_and_function_routes/inside_function")
            assertThat(response.bodyAsText()).isEqualTo("Inside class DELETED!")
        }

        @Test
        fun `routes through with inside class HEAD`() = configuredTestApplication {
            val response = client.head("/class_and_function_routes/inside_function")
            assertThat(response.bodyAsText()).isEqualTo("Inside class HEADED!")
        }

        @Test
        fun `routes through with inside class OPTIONS`() = configuredTestApplication {
            val response = client.options("/class_and_function_routes/inside_function")
            assertThat(response.bodyAsText()).isEqualTo("Inside class OPTIONED!")
        }
    }

    class OutsideClassTests {
        @Test
        fun `routes through with outside class GET`() = configuredTestApplication {
            val response = client.get("/class_and_function_routes/outside_function")
            assertThat(response.bodyAsText()).isEqualTo("Outside class GOTTED!")
        }

        @Test
        fun `routes through with outside class POST`() = configuredTestApplication {
            val response = client.post("/class_and_function_routes/outside_function")
            assertThat(response.bodyAsText()).isEqualTo("Outside class POSTED!")
        }

        @Test
        fun `routes through with outside class PUT`() = configuredTestApplication {
            val response = client.put("/class_and_function_routes/outside_function")
            assertThat(response.bodyAsText()).isEqualTo("Outside class PUTTED!")
        }

        @Test
        fun `routes through with outside class PATCH`() = configuredTestApplication {
            val response = client.patch("/class_and_function_routes/outside_function")
            assertThat(response.bodyAsText()).isEqualTo("Outside class PATCHED!")
        }

        @Test
        fun `routes through with outside class DELETE`() = configuredTestApplication {
            val response = client.delete("/class_and_function_routes/outside_function")
            assertThat(response.bodyAsText()).isEqualTo("Outside class DELETED!")
        }

        @Test
        fun `routes through with outside class HEAD`() = configuredTestApplication {
            val response = client.head("/class_and_function_routes/outside_function")
            assertThat(response.bodyAsText()).isEqualTo("Outside class HEADED!")
        }

        @Test
        fun `routes through with outside class OPTIONS`() = configuredTestApplication {
            val response = client.options("/class_and_function_routes/outside_function")
            assertThat(response.bodyAsText()).isEqualTo("Outside class OPTIONED!")
        }
    }
}