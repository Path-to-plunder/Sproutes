package com.casadetasha.kexp.sproute.processor.post.routes.segment

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.testing.*
import kotlin.test.Test

class MultiSprouteRootRoutesTest {

    @Test
    fun `first segment roots through`() = configuredTestApplication {
        val response = client.get("/multi")
        assertThat(response.bodyAsText()).isEqualTo("Multi get")
    }

    @Test
    fun `second segment roots through`() = configuredTestApplication {
        val response = client.get("/multi/root")
        assertThat(response.bodyAsText()).isEqualTo("Multi root get")
    }

    @Test
    fun `third segment roots through`() = configuredTestApplication {
        val response = client.get("/multi/root/sproute")
        assertThat(response.bodyAsText()).isEqualTo("Multi root sproute get")
    }
}
