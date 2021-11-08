package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.source.AuthenticationSourceFiles
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import kotlin.test.Test

class AuthenticatedSprouteCompilationTest {

    private lateinit var compilationResult: KotlinCompilation.Result

    @Test
    fun `Authenticated RouteRoot compiles with OK`() {
        compilationResult = compileSource(AuthenticationSourceFiles.boringRouteSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `Authenticated functions compiles with OK`() {
        compilationResult = compileSource(AuthenticationSourceFiles.functionRouteSource)
        assertThat(compilationResult).hasExitCode(OK)
    }
}
