package com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.helpers.Equality.LESSER
import com.casadetasha.kexp.sproute.processor.ktx.asVarArgs
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import javax.lang.model.element.Element
import javax.xml.datatype.DatatypeConstants.GREATER

internal sealed class SprouteAuthentication: Comparable<SprouteAuthentication> {
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

    abstract fun createChildFromElement(element: Element): SprouteAuthentication

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SprouteAuthentication) return false

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

    override fun compareTo(other: SprouteAuthentication): Int {
        return if (!this.isAuthenticationRequested && other.isAuthenticationRequested) {
            LESSER
        } else if (this.isAuthenticationRequested && !other.isAuthenticationRequested) {
            GREATER
        } else {
            compareToByParam(other)
        }
    }

    private fun compareToByParam(other: SprouteAuthentication): Int {
        val nameEquality = this.authenticationParamNames.compareTo(other.authenticationParamNames)

        return if (nameEquality != 0) {
            nameEquality
        } else if (this.isOptional) {
            LESSER
        } else {
            GREATER
        }
    }

    class BaseAuthentication : SprouteAuthentication() {
        override val isAuthenticationRequested: Boolean = false
        override val authenticationParamNames: String = ""
        override val isOptional: Boolean = false

        override fun createChildFromElement(element: Element): SprouteAuthentication {
            return ChildAuthentication(element, null)
        }
    }

    class ChildAuthentication constructor(
        private val element: Element,
        private val parentAuthenticatedAnnotation: Authenticated?
    ): SprouteAuthentication() {

        private val elementAuthenticatedAnnotation: Authenticated? = element.getAnnotation(Authenticated::class.java)
        private val elementUnauthenticatedAnnotation: Unauthenticated? =
            element.getAnnotation(Unauthenticated::class.java)
        private val authenticatedAnnotation: Authenticated? = with(elementAuthenticatedAnnotation) {
            validateAuthenticatedAnnotations()
            this ?: parentAuthenticatedAnnotation
        }

        override val isAuthenticationRequested: Boolean by lazy {
            val shouldAuthenticateAsDefault =
                elementUnauthenticatedAnnotation == null && parentAuthenticatedAnnotation != null
            elementAuthenticatedAnnotation != null || shouldAuthenticateAsDefault
        }

        private val authenticationNames: List<String> = authenticatedAnnotation?.names?.asList() ?: ArrayList()
        override val authenticationParamNames: String = listOf(authenticationNames.asVarArgs())
            .filter { it.isNotEmpty() }
            .joinToString(", ")

        override val isOptional: Boolean = elementAuthenticatedAnnotation?.optional == true

        override fun createChildFromElement(element: Element): ChildAuthentication {
            return ChildAuthentication(element, authenticatedAnnotation)
        }

        private fun validateAuthenticatedAnnotations() {
            if (elementAuthenticatedAnnotation != null && elementUnauthenticatedAnnotation != null) {
                SprouteAnnotationProcessor.processingEnvironment.printThenThrowError(
                    "Authenticated and Unauthenticated Annotations cannot be used together." +
                            " Cause: ${element.simpleName}."
                )
            }
        }
    }
}
