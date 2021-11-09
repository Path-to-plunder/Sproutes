package com.casadetasha.kexp.sproute.processor.sproutes

import com.casadetasha.kexp.annotationparser.KotlinValue.KotlinFunction
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.annotation_bridge.SprouteRequestAnnotationBridge.getInstaRequestAnnotation
import com.casadetasha.kexp.sproute.processor.annotation_bridge.SprouteRequestAnnotationBridge.getRequestMethodName
import com.casadetasha.kexp.sproute.processor.annotation_bridge.SprouteRequestAnnotationBridge.getRouteSegment
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.sproutes.authentication.Authentication
import com.casadetasha.kexp.sproute.processor.sproutes.segments.ProcessedRouteSegments.getSprouteRoot
import com.casadetasha.kexp.sproute.processor.sproutes.segments.RouteSegment
import com.casadetasha.kexp.sproute.processor.values.KotlinNames
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.VALID_EXTENSION_CLASSES
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.toRequestParamMemberNames
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.ktor.application.*

@OptIn(KotlinPoetMetadataPreview::class)
internal class SprouteRequestFunction(
    private val sprouteRootKey: TypeName? = null,
    kotlinFunction: KotlinFunction
): Comparable<SprouteRequestFunction> {
    private val routeSegment: RouteSegment by lazy { getSprouteRoot(sprouteRootKey) }

    private val baseRoutePath: String by lazy { routeSegment.getSproutePathForPackage(kotlinFunction.packageName) }

    val authentication: Authentication by lazy {
        routeSegment.authentication.createChildFromElement(kotlinFunction.element)
    }

    private val functionPathSegment: String by lazy { getRouteSegment(requestAnnotation).removeSuffix("/") }
    val fullRoutePath: String by lazy { baseRoutePath + functionPathSegment }

    val simpleName: String = kotlinFunction.simpleName
    val memberName: MemberName = kotlinFunction.memberName
    val params: List<MemberName> = kotlinFunction.parameters.toRequestParamMemberNames()
    val receiver: MemberName? = kotlinFunction.receiver.apply {}

    val isApplicationCallExtensionMethod: Boolean = receiver == ApplicationCall::class.toMemberName()
    val hasReturnValue: Boolean = kotlinFunction.hasReturnValue

    private val requestAnnotation: Annotation = kotlinFunction.element.getInstaRequestAnnotation()
    private val requestMethodSimpleName: String = getRequestMethodName(requestAnnotation)
    val requestMethodName: MemberName = MemberName(KotlinNames.KtorPackageNames.ROUTING, requestMethodSimpleName)

    init {
        failIfReceiverIsInvalid()
        failIfIsApplicationCallKtxWithReturnValue()
    }

    private fun failIfReceiverIsInvalid() {
        when (receiver) {
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

    private fun failIfIsApplicationCallKtxWithReturnValue() {
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
