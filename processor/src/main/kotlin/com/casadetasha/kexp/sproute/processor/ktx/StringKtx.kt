package com.casadetasha.kexp.sproute.processor.ktx


internal fun String.asPath() : String {
    return this.replace(".", "/")
}

internal fun String.asSubPackageOf(classPackage: String) : String {
    return this.removePrefix(classPackage)
}

internal fun String.asMethod() : String {
    return this.asPath().replace("/", "_")
}
