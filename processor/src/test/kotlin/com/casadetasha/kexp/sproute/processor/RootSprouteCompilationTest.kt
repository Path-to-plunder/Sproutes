package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.source.RootedSourceFiles
import com.casadetasha.kexp.sproute.processor.source.SourceFiles
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.*
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.Test

class RootSprouteCompilationTest {

    private lateinit var compilationResult: KotlinCompilation.Result

    @Test
    fun `single rooted source compiles with exit code OK`() {
        compilationResult = compileSource(RootedSourceFiles.singleRootedSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `package rooted source compiles with exit code OK`() {
        compilationResult = compileSource(RootedSourceFiles.packageRootedSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `multi rooted source compiles with exit code OK`() {
        compilationResult = compileSource(RootedSourceFiles.multiRootedSource)
        assertThat(compilationResult).hasExitCode(OK)
    }

    @Test
    fun `cyclical root source fails to compile`() {
        compilationResult = compileSource(RootedSourceFiles.cyclicalRootSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
    }

    @Test
    fun `cyclical root with gap source fails to compile`() {
        compilationResult = compileSource(RootedSourceFiles.cyclicalRootWithGapSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
    }

    @Test
    fun `self referential rooted source fails to compile`() {
        compilationResult = compileSource(RootedSourceFiles.selfReferentialRootSource)
        assertThat(compilationResult).hasExitCode(INTERNAL_ERROR)
    }
}
