package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.ktx.asVarArgs
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import javax.lang.model.element.Element

internal sealed class SprouteAuthentication: Comparable<SprouteAuthentication> {
    abstract val isAuthenticationRequested: Boolean
    abstract val hasAuthenticationParams: Boolean
    abstract val authenticationParams: String

    companion object {
        const val LESSER = -1
        const val GREATER = 1
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
            this.authenticationParams.compareTo(other.authenticationParams)
        }
    }

    class BaseAuthentication : SprouteAuthentication() {
        override val isAuthenticationRequested: Boolean = false
        override val hasAuthenticationParams: Boolean = false
        override val authenticationParams: String = ""

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

        private val authenticationNames: List<String> = authenticatedAnnotation?.names?.asList() ?: ArrayList()
        private val authenticationOptionalParamSuffix: String by lazy {
            when (elementAuthenticatedAnnotation?.optional ?: false) {
                true -> "optional = true"
                false -> ""
            }
        }

        override val isAuthenticationRequested: Boolean by lazy {
            val shouldAuthenticateAsDefault =
                elementUnauthenticatedAnnotation == null && parentAuthenticatedAnnotation != null
            elementAuthenticatedAnnotation != null || shouldAuthenticateAsDefault
        }

        override val hasAuthenticationParams: Boolean =
            isAuthenticationRequested || elementAuthenticatedAnnotation?.optional != null

        override val authenticationParams: String = listOf(authenticationNames.asVarArgs(), authenticationOptionalParamSuffix)
            .filter { it.isNotEmpty() }
            .joinToString(", ")

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
