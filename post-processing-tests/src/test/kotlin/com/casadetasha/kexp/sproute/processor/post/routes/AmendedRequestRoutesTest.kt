package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.test.Test

class AmendedRequestRoutesTest {
    @Test
    fun `routes through with GET`() = configuredTestApplication {
        val response = client.get("/amended_routes_segment/amended_routes")
        assertThat(response.bodyAsText()).isEqualTo("Amended GET.")
    }

    @Test
    fun `routes through with POST`() = configuredTestApplication {
        val response = client.post("/amended_routes_segment/amended_routes")
        assertThat(response.bodyAsText()).isEqualTo("Amended POST.")
    }

    @Test
    fun `routes through with PUT`() = configuredTestApplication {
        val response = client.put("/amended_routes_segment/amended_routes")
        assertThat(response.bodyAsText()).isEqualTo("Amended PUT.")
    }

    @Test
    fun `routes through with PATCH`() = configuredTestApplication {
        val response = client.patch("/amended_routes_segment/amended_routes")
        assertThat(response.bodyAsText()).isEqualTo("Amended PATCH.")
    }

    @Test
    fun `routes through with DELETE`() = configuredTestApplication {
        val response = client.delete("/amended_routes_segment/amended_routes")
        assertThat(response.bodyAsText()).isEqualTo("Amended DELETE.")
    }

    @Test
    fun `routes through with HEAD`() = configuredTestApplication {
        val response = client.head("/amended_routes_segment/amended_routes")
        assertThat(response.bodyAsText()).isEqualTo("Amended HEAD.")
    }

    @Test
    fun `routes through with OPTIONS`() = configuredTestApplication {
        val response = client.options("/amended_routes_segment/amended_routes")
        assertThat(response.bodyAsText()).isEqualTo("Amended OPTIONS.")
    }

    @Test
    fun `does not route through with original name GET`() = configuredTestApplication {
        val response = client.get("/amended_routes_segment")
        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `does not route through with original name POST`() = configuredTestApplication {
        val response = client.post("/amended_routes_segment")
        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `does not route through with original name PUT`() = configuredTestApplication {
        val response = client.put("/amended_routes_segment")
        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `does not route through with original name PATCH`() = configuredTestApplication {
        val response = client.patch("/amended_routes_segment")
        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `does not route through with original name DELETE`() = configuredTestApplication {
        val response = client.delete("/amended_routes_segment")
        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `does not route through with original name HEAD`() = configuredTestApplication {
        val response = client.head("/amended_routes_segment")
        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `does not route through with original name OPTIONS`() = configuredTestApplication {
        val response = client.options("/amended_routes_segment")
        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }
}