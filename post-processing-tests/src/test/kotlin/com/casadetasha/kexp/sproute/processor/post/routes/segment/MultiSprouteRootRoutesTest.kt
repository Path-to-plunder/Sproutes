package com.casadetasha.kexp.sproute.processor.post.routes.segment

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.withConfiguredTestApplication
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.testing.*
import kotlin.test.Test

class MultiSprouteRootRoutesTest {

    @Test
    fun `first segment roots through`() = withConfiguredTestApplication {
        handleRequest(Get, "/multi").apply {
            assertThat(response.content).isEqualTo("Multi get")
        }
    }

    @Test
    fun `second segment roots through`() = withConfiguredTestApplication {
        handleRequest(Get, "/multi/root").apply {
            assertThat(response.content).isEqualTo("Multi root get")
        }
    }

    @Test
    fun `third segment roots through`() = withConfiguredTestApplication {
        handleRequest(Get, "/multi/root/sproute").apply {
            assertThat(response.content).isEqualTo("Multi root sproute get")
        }
    }
}
