package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.ktx.asVarArgs
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import javax.lang.model.element.Element

class Authentication(
    private val element: Element,
    private val parentAuthenticatedAnnotation: Authenticated?
) {

    private val elementAuthenticatedAnnotation: Authenticated? = element.getAnnotation(Authenticated::class.java)
    private val elementUnauthenticatedAnnotation: Unauthenticated? = element.getAnnotation(Unauthenticated::class.java)
    private val authenticatedAnnotation: Authenticated? = with(elementAuthenticatedAnnotation) {
        validateAuthenticatedAnnotations()
        this ?: parentAuthenticatedAnnotation
    }

    private val authenticationNames: List<String> = authenticatedAnnotation?.names?.asList()?: ArrayList()

    private val authenticationOptionalParamSuffix: String by lazy {
        when (elementAuthenticatedAnnotation?.optional ?: false) {
            true -> "optional = true"
            false -> ""
        }
    }

    val isAuthenticationRequested: Boolean by lazy {
        val shouldAuthenticateAsDefault = elementUnauthenticatedAnnotation == null && parentAuthenticatedAnnotation != null
        elementAuthenticatedAnnotation != null || shouldAuthenticateAsDefault
    }
    val authenticationParams: String = listOf(authenticationNames.asVarArgs(), authenticationOptionalParamSuffix)
        .filter { it.isNotEmpty() }
        .joinToString(", ")
    val hasAuthenticationParams: Boolean = isAuthenticationRequested || elementAuthenticatedAnnotation?.optional != null

    private fun validateAuthenticatedAnnotations() {
        if (elementAuthenticatedAnnotation != null && elementUnauthenticatedAnnotation != null) {
            SprouteAnnotationProcessor.processingEnvironment.printThenThrowError(
                "Authenticated and Unauthenticated Annotations cannot be used together." +
                        " Cause: ${element.simpleName}."
            )
        }
    }

    fun forChildElement(element: Element): Authentication {
        return Authentication(element, authenticatedAnnotation)
    }
}
