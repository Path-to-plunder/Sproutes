package com.casadetasha.kexp.sproute.processor.ktx

import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

internal fun ProcessingEnvironment.printNote(noteText: String) {
    messager.printMessage(Diagnostic.Kind.NOTE, noteText)
}

internal fun ProcessingEnvironment.printThenThrowError(errorMessage: String): Nothing {
    messager.printMessage(Diagnostic.Kind.ERROR, errorMessage)
    throw IllegalArgumentException(errorMessage)
}
