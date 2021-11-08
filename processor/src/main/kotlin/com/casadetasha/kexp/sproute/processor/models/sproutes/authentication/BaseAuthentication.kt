package com.casadetasha.kexp.sproute.processor.models.sproutes.authentication

import javax.lang.model.element.Element

internal class BaseAuthentication : Authentication() {
    override val isAuthenticationRequested: Boolean = false
    override val authenticationParamNames: String = ""
    override val isOptional: Boolean = false

    override fun createChildFromElement(element: Element): Authentication {
        return SubAuthentication(element, null)
    }
}
