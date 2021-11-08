package com.casadetasha.kexp.sproute.processor.models.sproutes.authentication

import com.casadetasha.kexp.sproute.processor.models.Equality.GREATER
import com.casadetasha.kexp.sproute.processor.models.Equality.LESSER
import javax.lang.model.element.Element

internal sealed class Authentication: Comparable<Authentication> {
    companion object {
        const val OPTIONAL_PARAMETER_VALUE: String = "optional = true"
    }
    abstract val isAuthenticationRequested: Boolean
    val hasAuthenticationParams: Boolean by lazy { authenticationParamNames.isNotBlank() || isOptional }

    abstract val isOptional: Boolean
    protected abstract val authenticationParamNames: String
    val authenticationParams: String by lazy {
        if (authenticationParamNames.isBlank() && isOptional) {
            OPTIONAL_PARAMETER_VALUE
        } else {
            authenticationParamNames + if (isOptional) ", $OPTIONAL_PARAMETER_VALUE" else ""
        }
    }

    abstract fun createChildFromElement(element: Element): Authentication

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Authentication) return false

        if (isAuthenticationRequested != other.isAuthenticationRequested) return false
        if (hasAuthenticationParams != other.hasAuthenticationParams) return false
        if (authenticationParams != other.authenticationParams) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isAuthenticationRequested.hashCode()
        result = 31 * result + hasAuthenticationParams.hashCode()
        result = 31 * result + authenticationParams.hashCode()
        return result
    }

    override fun compareTo(other: Authentication): Int {
        return if (!this.isAuthenticationRequested && other.isAuthenticationRequested) {
            LESSER
        } else if (this.isAuthenticationRequested && !other.isAuthenticationRequested) {
            GREATER
        } else {
            compareToByParam(other)
        }
    }

    private fun compareToByParam(other: Authentication): Int {
        val nameEquality = this.authenticationParamNames.compareTo(other.authenticationParamNames)

        return if (nameEquality != 0) {
            nameEquality
        } else if (this.isOptional) {
            LESSER
        } else {
            GREATER
        }
    }
}
