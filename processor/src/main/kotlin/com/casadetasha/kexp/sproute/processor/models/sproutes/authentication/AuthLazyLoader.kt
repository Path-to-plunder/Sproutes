package com.casadetasha.kexp.sproute.processor.models.sproutes.authentication

import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.ProcessedSprouteSegments
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.SprouteSegment
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.Element

internal class AuthLazyLoader(
    parentRootKey: TypeName,
    element: Element
) {
    val value: Authentication by lazy {
        ProcessedSprouteSegments.getSprouteRoot(parentRootKey)
            .authentication
            .createChildFromElement(element)
    }
}