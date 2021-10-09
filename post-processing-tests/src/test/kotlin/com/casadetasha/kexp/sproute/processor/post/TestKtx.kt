package com.casadetasha.kexp.sproute.processor.post

import com.casadetasha.kexp.sproute.configureSproutes
import io.ktor.server.testing.*

internal fun withConfiguredTestApplication(moduleFunction: TestApplicationEngine.() -> Unit) {
    return withTestApplication({ configureSproutes() }, moduleFunction)
}
