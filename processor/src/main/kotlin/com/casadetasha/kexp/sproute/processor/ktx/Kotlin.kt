package com.casadetasha.kexp.sproute.processor.ktx

internal fun Boolean.orElse(function: () -> Unit) {
    if (!this) function()
}

internal fun String.asPath() : String {
    return this.replace(".", "/")
}

internal fun String.asSubPackageOf(classPackage: String) : String {
    return this.removePrefix(classPackage)
}

internal fun String.asMethod() : String {
    return this.asPath().replace("/", "_")
}

internal fun List<String>.asVarArgs(): String = this.let {
    return joinToString(", ") { "\"$it\"" }
}

internal fun <T> List<T>?.ifNotEmpty(function: (List<T>) -> Unit): Boolean {
    if (this == null || isEmpty()) {
        return false
    }

    function(this)
    return true
}
