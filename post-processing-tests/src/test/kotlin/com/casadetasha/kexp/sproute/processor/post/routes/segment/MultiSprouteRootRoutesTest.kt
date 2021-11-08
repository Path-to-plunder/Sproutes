package com.casadetasha.kexp.sproute.processor.post.routes.segment

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