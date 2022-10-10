package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.source.MethodParamSourceFiles
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import kotlin.test.Test

class MethodParamSprouteCompilationTest {

    private lateinit var compilationResult: KotlinCompilation.Result

    @Test
    fun `class param Routes compile with OK`() {
        compilationResult = compileSource(MethodParamSourceFiles.classParamSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `method param Routes compile with OK`() {
        compilationResult = compileSource(MethodParamSourceFiles.methodParamSource)
        assertThat(compilationResult).hasExitCode(OK)
    }
}
