package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.annotationparser.KotlinFunction
import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.processor.MemberNames
import com.casadetasha.kexp.sproute.processor.MemberNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.getInstaRequestAnnotation
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.getRequestMethodName
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.getRouteSegment
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.shouldIncludeClassRouteSegment
import com.casadetasha.kexp.sproute.processor.ktx.asMethod
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.ktor.application.*
import io.ktor.routing.*

@OptIn(KotlinPoetMetadataPreview::class)
internal class SprouteRequestFunction(
    kotlinFunction: KotlinFunction,
    pathRootSegment: String,
    classRouteSegment: String,
    parentAuthentication: Authenticated?
) : Comparable<SprouteRequestFunction> {

    companion object {
        val VALID_EXTENSION_CLASSES =
            listOf(
                ApplicationCall::class.toMemberName(),
                Route::class.toMemberName()
            )
    }

    val fullRoutePath: String by lazy {
        val includeClassRouteSegment: Boolean = shouldIncludeClassRouteSegment(requestAnnotation)
        val pathSuffix: String = getRouteSegment(requestAnnotation)
        val usableClassSegment: String = if (includeClassRouteSegment) classRouteSegment else ""
        pathRootSegment + usableClassSegment + pathSuffix
    }

    val simpleName: String = kotlinFunction.simpleName
    val memberName: MemberName = kotlinFunction.memberName
    val params: List<MemberName> = kotlinFunction.parameters.toRequestParamMemberNames()
    val receiver: MemberName? = kotlinFunction.receiver.apply { validateFunctionReceiver(this) }

    private val requestAnnotation: Annotation = kotlinFunction.element.getInstaRequestAnnotation()

    private val authAnnotations = AuthAnnotations(kotlinFunction.element, parentAuthentication)
    val isAuthenticationRequested: Boolean = authAnnotations.isAuthenticationRequested
    val authenticationParams: String = authAnnotations.authenticationParams
    val hasAuthenticationParams: Boolean = authAnnotations.hasAuthenticationParams

    private val requestMethodSimpleName: String = getRequestMethodName(requestAnnotation)
    val requestMethodName: MemberName = MemberName(MemberNames.KtorPackageNames.ROUTING, requestMethodSimpleName)
    val configurationMethodSimpleName by lazy {
        val formattedRoute = fullRoutePath.removePrefix("/").asMethod()
        val routePrefix = if (formattedRoute.isNotBlank()) "${formattedRoute}_" else ""
        val requestType = requestMethodSimpleName.uppercase()
        "configureRequestRoute\$${routePrefix}$requestType"
    }

    val isApplicationCallExtensionMethod: Boolean = receiver == ApplicationCall::class.toMemberName()
    val hasReturnValue: Boolean = kotlinFunction.hasReturnValue.apply { validateReturnValue(this) }

    private fun validateFunctionReceiver(memberName: MemberName?) {
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

    private fun validateReturnValue(hasReturnValue: Boolean) {
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
