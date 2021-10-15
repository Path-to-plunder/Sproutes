package com.casadetasha.kexp.sproute.processor

import assertk.Assert
import assertk.fail
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.*
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.Test

class AuthenticatedSprouteCompilationTest {
    private lateinit var compilationResult: KotlinCompilation.Result

    @Test
    fun `Authenticated RouteRoot compiles with OK`() {
        compileSource(AuthenticationSourceFiles.boringRouteSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    private fun compileSource(vararg sourceFiles: SourceFile) {
        compilationResult = KotlinCompilation().apply {
            sources = sourceFiles.toList()
            annotationProcessors = listOf(SprouteAnnotationProcessor())
            inheritClassPath = true
            messageOutputStream = System.out
        }.compile()
    }
}
