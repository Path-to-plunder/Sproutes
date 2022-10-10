package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.source.SourceFiles
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.*
import kotlin.test.Test

class SprouteProcessorTest {

    private lateinit var compilationResult: KotlinCompilation.Result

    @Test
    fun `boring route compiles with exit code OK`() {
        compilationResult = compileSource(SourceFiles.boringRouteSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `added parameter routes compile with exit code OK`() {
        compilationResult = compileSource(SourceFiles.addedParameterRouteSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `function routes compile with exit code OK`() {
        compilationResult = compileSource(SourceFiles.functionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `multiple function routes compile with exit code OK`() {
        compilationResult = compileSource(SourceFiles.functionSource, SourceFiles.routeAndFunctionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `function routes with overlapping routes and requests cause compilation error`() {
        compilationResult = compileSource(SourceFiles.functionSource, SourceFiles.clonedFunctionSource)
        assertThat(compilationResult).hasExitCode(COMPILATION_ERROR)
    }

    @Test
    fun `multi function routes compile with exit code OK`() {
        compilationResult = compileSource(SourceFiles.multiFunctionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `route extension function routes compile with exit code OK`() {
        compilationResult = compileSource(SourceFiles.routeExtensionFunctionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `application call extension function routes compile with exit code OK`() {
        compilationResult = compileSource(SourceFiles.applicationCallExtensionFunctionSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `unrecognized extension function routes throw exception while compiling`() {
        compilationResult = compileSource(SourceFiles.unrecognizedExtensionFunctionSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
    }

    @Test
    fun `applicationCall extension functions that return a value route throw exception while compiling`() {
        compilationResult = compileSource(SourceFiles.returnValueApplicationCallExtensionFunctionSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
    }

    @Test
    fun `compiling duplicate routes with different requests does not throw exception`() {
        compilationResult = compileSource(SourceFiles.duplicateRouteDifferentRequestSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `compiling duplicate request and route pairings throws exception`() {
        compilationResult = compileSource(SourceFiles.duplicateRouteSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
    }

    @Test
    fun `compiling duplicate request and route pairings with different path param names throws exception`() {
        compilationResult = compileSource(SourceFiles.duplicatePathParamRouteSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
    }
}
