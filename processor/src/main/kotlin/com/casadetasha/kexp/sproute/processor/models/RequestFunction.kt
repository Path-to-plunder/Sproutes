package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.MemberNames
import com.casadetasha.kexp.sproute.processor.MemberNames.convertToMemberNames
import com.casadetasha.kexp.sproute.processor.RequestAnnotations.getInstaRequestAnnotation
import com.casadetasha.kexp.sproute.processor.RequestAnnotations.getRequestMethodName
import com.casadetasha.kexp.sproute.processor.RequestAnnotations.getRouteSegment
import com.casadetasha.kexp.sproute.processor.RequestAnnotations.shouldIncludeClassRouteSegment
import com.casadetasha.kexp.sproute.processor.ktx.asMethod
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.ImmutableKmFunction
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.metadata.KmClassifier
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import kotlin.reflect.KClass

@OptIn(KotlinPoetMetadataPreview::class)
internal class RequestFunction(
    private val processingEnv: ProcessingEnvironment,
    methodElement: Element,
    function: ImmutableKmFunction,
    pathRootSegment: String,
    classRouteSegment: String,
    defaultAuthenticationStatus: KClass<*>
) : Comparable<RequestFunction> {

    companion object {
        val VALID_EXTENSION_CLASSES =
            listOf(
                ApplicationCall::class.toMemberName(),
                Route::class.toMemberName()
            )
    }

    private val requestAnnotation: Annotation = methodElement.getInstaRequestAnnotation()
    private val authenticatedAnnotation: Authenticated? = methodElement.getAnnotation(Authenticated::class.java)
    private val unauthenticatedAnnotation: Unauthenticated? = methodElement.getAnnotation(Unauthenticated::class.java)
    val authenticationName: String? = authenticatedAnnotation?.apply {
        checkAuthenticatedAnnotationValidity()
    }?.name

    val fullRoutePath: String by lazy {
        val includeClassRouteSegment: Boolean = shouldIncludeClassRouteSegment(requestAnnotation)
        val pathSuffix: String = getRouteSegment(requestAnnotation)
        val usableClassSegment: String = if (includeClassRouteSegment) classRouteSegment else ""
        pathRootSegment + usableClassSegment + pathSuffix
    }

    private val requestMethodSimpleName: String = getRequestMethodName(requestAnnotation)
    val requestMethodName: MemberName = MemberName(MemberNames.KtorPackageNames.ROUTING, requestMethodSimpleName)
    val functionSimpleName: String = function.name
    val configurationMethodSimpleName by lazy {
        val formattedRoute = fullRoutePath.removePrefix("/").asMethod()
        val routePrefix = if (formattedRoute.isNotBlank()) "${formattedRoute}_" else ""
        val requestType = requestMethodSimpleName.uppercase()
        "configureRequestRoute\$${routePrefix}$requestType"
    }

    val functionReceiver: MemberName? by lazy {
        val receiverType = function.receiverParameterType;
        if (receiverType == null) null
        else when (receiverType.classifier) {
            is KmClassifier.Class -> receiverType.toMemberName()
            else -> processingEnv.printThenThrowError(
                "Unable to generate $configurationMethodSimpleName, extension parameter must be a class."
            )
        }.apply {
            when (this) {
                !in VALID_EXTENSION_CLASSES -> {
                    val extensionClasses = VALID_EXTENSION_CLASSES.joinToString(", ") { it.canonicalName }
                    processingEnv.printThenThrowError("Only [$extensionClasses] are supported as extension" +
                            " receivers for request methods. Found $this for route $fullRoutePath")
                }
            }
        }
    }

    val isApplicationCallExtensionMethod: Boolean = functionReceiver == ApplicationCall::class.toMemberName()
    val hasReturnValue: Boolean = (function.returnType.toMemberName() != Unit::class.toMemberName()).apply {
        if (this && isApplicationCallExtensionMethod) processingEnv.printThenThrowError("Route" +
                " $fullRoutePath is invalid. Routes cannot both be an ApplicationCall Extension method AND have a" +
                " return type. If you want to access the ApplicationCall and return a value, add the ApplicationCall" +
                " as a method parameter.")
    }

    val functionParams: List<MemberName> = function.valueParameters.convertToMemberNames()

    val isAuthenticationRequested: Boolean by lazy {
        val shouldAuthenticateAsDefault = unauthenticatedAnnotation == null
                && defaultAuthenticationStatus == Authenticated::class

        authenticatedAnnotation != null || shouldAuthenticateAsDefault
    }

    private fun checkAuthenticatedAnnotationValidity() {
        if (authenticatedAnnotation != null && unauthenticatedAnnotation != null) {
            processingEnv.printThenThrowError(
                "Authenticated and Unauthenticated Annotations cannot be used together."
            )
        }
    }

    override fun compareTo(other: RequestFunction): Int {
        val comparison = fullRoutePath.compareTo(other.fullRoutePath)
        return if (comparison != 0) comparison else requestMethodSimpleName.compareTo(other.requestMethodSimpleName)
    }
}
