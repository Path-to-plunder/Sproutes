package com.casadetasha.kexp.sproute.processor

import assertk.Assert
import assertk.fail
import com.tschuchort.compiletesting.KotlinCompilation

internal fun assertThat(result: KotlinCompilation.Result?) : Assert<KotlinCompilation.Result> {
    if (result == null) fail ("Results must be compiled before comparing result value")
    return assertk.assertThat(result)
}

internal fun Assert<KotlinCompilation.Result>.hasExitCode(exitCode: KotlinCompilation.ExitCode) = given { compilationResult ->
    if (compilationResult.exitCode != exitCode) fail(
        "Expected exitCode $exitCode but found ${compilationResult.exitCode}"
    )
}
