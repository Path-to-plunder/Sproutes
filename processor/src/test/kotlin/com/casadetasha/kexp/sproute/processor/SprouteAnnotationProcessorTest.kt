package com.casadetasha.kexp.sproute.processor

import assertk.Assert
import assertk.fail
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.*
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.Test

class SprouteAnnotationProcessorTest {
    private lateinit var compilationResult: KotlinCompilation.Result

    @Test
    fun `boring route compiles with exit code OK`() {
        compileSource(SourceFiles.boringRouteSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `added parameter routes compile with exit code OK`() {
        compileSource(SourceFiles.addedParameterRouteSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `function routes compile with exit code OK`() {
        compileSource(SourceFiles.functionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `multiple function routes compile with exit code OK`() {
        compileSource(SourceFiles.functionSource, SourceFiles.routeAndFunctionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `function routes with overlapping routes and requests cause compilation error`() {
        compileSource(SourceFiles.functionSource, SourceFiles.clonedFunctionSource)
        assertThat(compilationResult).hasExitCode(COMPILATION_ERROR)
    }

    @Test
    fun `multi function routes compile with exit code OK`() {
        compileSource(SourceFiles.multiFunctionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `route extension function routes compile with exit code OK`() {
        compileSource(SourceFiles.routeExtensionFunctionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `application call extension function routes compile with exit code OK`() {
        compileSource(SourceFiles.applicationCallExtensionFunctionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `unrecognized extension function routes throw exception while compiling`() {
        compileSource(SourceFiles.unrecognizedExtensionFunctionSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
    }

    @Test
    fun `applicationCall extension functions that return a value route throw exception while compiling`() {
        compileSource(SourceFiles.returnValueApplicationCallExtensionFunctionSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
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
