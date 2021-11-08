package com.casadetasha.kexp.sproute.processor.models.sproutes

import com.casadetasha.kexp.annotationparser.KotlinValue.KotlinFunction
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.models.KotlinNames
import com.casadetasha.kexp.sproute.processor.models.KotlinNames.VALID_EXTENSION_CLASSES
import com.casadetasha.kexp.sproute.processor.models.KotlinNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestAnnotations.getInstaRequestAnnotation
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestAnnotations.getRequestMethodName
import com.casadetasha.kexp.sproute.processor.models.SprouteRequestAnnotations.getRouteSegment
import com.casadetasha.kexp.sproute.processor.models.sproutes.authentication.Authentication
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.ProcessedSprouteSegments.getSprouteRoot
import com.casadetasha.kexp.sproute.processor.models.sproutes.roots.SprouteSegment
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.ktor.application.*

@OptIn(KotlinPoetMetadataPreview::class)
internal class SprouteRequestFunction(
    private val sprouteRootKey: TypeName? = null,
    kotlinFunction: KotlinFunction
): Comparable<SprouteRequestFunction> {
    private val sprouteSegment: SprouteSegment by lazy { getSprouteRoot(sprouteRootKey) }

    private val baseRoutePath: String by lazy { sprouteSegment.getSproutePathForPackage(kotlinFunction.packageName) }

    val authentication: Authentication by lazy {
        sprouteSegment.authentication.createChildFromElement(kotlinFunction.element)
    }

    private val functionPathSegment: String by lazy { getRouteSegment(requestAnnotation).removeSuffix("/") }
    val fullRoutePath: String by lazy { baseRoutePath + functionPathSegment }

    val simpleName: String = kotlinFunction.simpleName
    val memberName: MemberName = kotlinFunction.memberName
    val params: List<MemberName> = kotlinFunction.parameters.toRequestParamMemberNames()
    val receiver: MemberName? = kotlinFunction.receiver.apply { failIfFunctionReceiverIsInvalid(this) }

    val hasReturnValue: Boolean = kotlinFunction.hasReturnValue.apply { failIfReturnValueIsInvalid(this) }
    val isApplicationCallExtensionMethod: Boolean = receiver == ApplicationCall::class.toMemberName()

    private val requestAnnotation: Annotation = kotlinFunction.element.getInstaRequestAnnotation()
    private val requestMethodSimpleName: String = getRequestMethodName(requestAnnotation)
    val requestMethodName: MemberName = MemberName(KotlinNames.KtorPackageNames.ROUTING, requestMethodSimpleName)

    private fun failIfFunctionReceiverIsInvalid(memberName: MemberName?) {
        when (memberName) {
            null -> return
            !in VALID_EXTENSION_CLASSES -> {
                val extensionClasses = VALID_EXTENSION_CLASSES.joinToString(", ") { it.canonicalName }
                processingEnvironment.printThenThrowError(
                    "Only [$extensionClasses] are supported as extension" +
                            " receivers for request methods. Found $this for route $fullRoutePath"
                )
            }
        }
    }

    private fun failIfReturnValueIsInvalid(hasReturnValue: Boolean) {
        if (hasReturnValue && isApplicationCallExtensionMethod) processingEnvironment.printThenThrowError(
            "Route $fullRoutePath is invalid. Routes cannot both be an ApplicationCall Extension method AND" +
                    " have a return type. If you want to access the ApplicationCall and return a value, add the" +
                    " ApplicationCall as a method parameter."
        )
    }

    override fun compareTo(other: SprouteRequestFunction): Int {
        val comparison = fullRoutePath.compareTo(other.fullRoutePath)
        return if (comparison != 0) comparison else requestMethodSimpleName.compareTo(other.requestMethodSimpleName)
    }
}
