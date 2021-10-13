package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.ktx.asVarArgs
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import kotlin.reflect.KClass

class AuthenticationAnnotations(private val authenticatedAnnotation: Authenticated?,
                                private val unauthenticatedAnnotation: Unauthenticated?,
                                private val defaultAuthenticationStatus: KClass<*>
) {
    private val authenticationNames: List<String> = authenticatedAnnotation?.apply {
        validateAuthenticatedAnnotations()
    }?.names?.asList()?: ArrayList()

    private val authenticationOptionalParamSuffix: String by lazy {
        when (authenticatedAnnotation?.optional ?: false) {
            true -> "optional = true"
            false -> ""
        }
    }

    val isAuthenticationRequested: Boolean by lazy {
        val shouldAuthenticateAsDefault = unauthenticatedAnnotation == null
                && defaultAuthenticationStatus == Authenticated::class

        authenticatedAnnotation != null || shouldAuthenticateAsDefault
    }
    val authenticationParams: String = listOf(authenticationNames.asVarArgs(), authenticationOptionalParamSuffix)
        .filter { it.isNotEmpty() }
        .joinToString(", ")
    val hasAuthenticationParams: Boolean = isAuthenticationRequested || authenticatedAnnotation?.optional != null

    private fun validateAuthenticatedAnnotations() {
        if (authenticatedAnnotation != null && unauthenticatedAnnotation != null) {
            SprouteAnnotationProcessor.processingEnvironment.printThenThrowError(
                "Authenticated and Unauthenticated Annotations cannot be used together."
            )
        }
    }
}
