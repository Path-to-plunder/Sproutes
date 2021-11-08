package com.casadetasha.kexp.sproute.processor.sproutes.authentication

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor
import com.casadetasha.kexp.sproute.processor.ktx.asVarArgs
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import javax.lang.model.element.Element

internal class SubAuthentication constructor(
    private val element: Element,
    private val parentAuthenticatedAnnotation: Authenticated?
): Authentication() {

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

    override fun createChildFromElement(element: Element): SubAuthentication {
        return SubAuthentication(element, authenticatedAnnotation)
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
