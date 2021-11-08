package com.casadetasha.kexp.sproute.processor.sproutes.authentication

import com.casadetasha.kexp.sproute.processor.sproutes.segments.ProcessedRouteSegments
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.Element

internal class AuthLazyLoader(
    parentRootKey: TypeName,
    element: Element
) {
    val value: Authentication by lazy {
        ProcessedRouteSegments.getSprouteRoot(parentRootKey)
            .authentication
            .createChildFromElement(element)
    }
}