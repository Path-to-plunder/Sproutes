package com.casadetasha.kexp.sproute.processor.post.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.casadetasha.kexp.sproute.processor.post.configuredTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test

class ParamRequestRoutesTest {

    @Test
    fun `captures class and function path parameters through with GET`() = configuredTestApplication {
        val response = client.get("/param_route/test-param/test-get-param")
        assertThat(response.bodyAsText()).isEqualTo("Path parammed GET: test-param | test-get-param.")
    }

    @Test
    fun `captures query parameters through with GET`() = configuredTestApplication {
        val response = client.get("/param_route/query_param_test?renamed-param=not-gunna-guess-it&param=it-it-it")
        assertThat(response.bodyAsText()).isEqualTo("Query parammed GET: not-gunna-guess-it | it-it-it.")
    }
}
