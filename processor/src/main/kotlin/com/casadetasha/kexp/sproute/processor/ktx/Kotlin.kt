package com.casadetasha.kexp.sproute.processor.ktx

import com.google.common.collect.ImmutableSet
import javax.lang.model.element.Element

internal fun Boolean.orElseRun(function: () -> Unit) {
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

internal fun <T> List<T>?.letIfNotEmpty(function: (List<T>) -> Unit): Boolean {
    if (this == null || isEmpty()) {
        return false
    }
    function(this)
    return true
}

internal fun MutableList<Element>.toMap() = HashMap<String, Element>().apply {
    this@toMap.forEach { this[it.simpleName.toString()] = it }
}

internal fun <K, V> HashMap<K, MutableList<V>>.getOrCreateList(key: K): MutableList<V> {
    this[key] = this[key] ?: ArrayList()
    return this[key]!!
}

internal fun <T, R> Iterable<T>.mapToImmutableSet(transform: (T) -> R): ImmutableSet<R> {
    return map(transform)
        .toImmutableSet()
}

internal fun <T> Iterable<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet.copyOf(toSet())
