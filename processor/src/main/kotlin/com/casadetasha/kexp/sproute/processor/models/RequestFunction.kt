package com.casadetasha.kexp.sproute.processor.models

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Unauthenticated
import com.casadetasha.kexp.sproute.processor.MemberNames
import com.casadetasha.kexp.sproute.processor.MemberNames.convertToMemberNames
import com.casadetasha.kexp.sproute.processor.SprouteProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.getInstaRequestAnnotation
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.getRequestMethodName
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.getRouteSegment
import com.casadetasha.kexp.sproute.processor.SprouteRequestAnnotations.shouldIncludeClassRouteSegment
import com.casadetasha.kexp.sproute.processor.ktx.asMethod
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.ImmutableKmFunction
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.metadata.KmClassifier
import javax.lang.model.element.Element
import kotlin.reflect.KClass

@OptIn(KotlinPoetMetadataPreview::class)
internal class RequestFunction(
    packageName: String,
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

    val fullRoutePath: String by lazy {
        val includeClassRouteSegment: Boolean = shouldIncludeClassRouteSegment(requestAnnotation)
        val pathSuffix: String = getRouteSegment(requestAnnotation)
        val usableClassSegment: String = if (includeClassRouteSegment) classRouteSegment else ""
        pathRootSegment + usableClassSegment + pathSuffix
    }

    private val requestMethodSimpleName: String = getRequestMethodName(requestAnnotation)
    val requestMethodName: MemberName = MemberName(MemberNames.KtorPackageNames.ROUTING, requestMethodSimpleName)
    val simpleName: String = function.name
    val memberName: MemberName = MemberName(packageName, simpleName)
    val configurationMethodSimpleName by lazy {
        val formattedRoute = fullRoutePath.removePrefix("/").asMethod()
        val routePrefix = if (formattedRoute.isNotBlank()) "${formattedRoute}_" else ""
        val requestType = requestMethodSimpleName.uppercase()
        "configureRequestRoute\$${routePrefix}$requestType"
    }

    val functionReceiver: MemberName? by lazy {
        val receiverType = function.receiverParameterType
        if (receiverType == null) null
        else when (receiverType.classifier) {
            is KmClassifier.Class -> receiverType.toMemberName()
            else -> processingEnvironment.printThenThrowError(
                "Unable to generate $configurationMethodSimpleName, extension parameter must be a class."
            )
        }.apply { validateFunctionReceiver(this) }
    }

    val isApplicationCallExtensionMethod: Boolean = functionReceiver == ApplicationCall::class.toMemberName()
    val hasReturnValue: Boolean = (function.returnType.toMemberName() != Unit::class.toMemberName()).apply {
        if (this && isApplicationCallExtensionMethod) processingEnvironment.printThenThrowError("Route" +
                " $fullRoutePath is invalid. Routes cannot both be an ApplicationCall Extension method AND have a" +
                " return type. If you want to access the ApplicationCall and return a value, add the ApplicationCall" +
                " as a method parameter.")
    }

    val functionParams: List<MemberName> = function.valueParameters.convertToMemberNames()

    private fun validateAuthenticatedAnnotations() {
        if (authenticatedAnnotation != null && unauthenticatedAnnotation != null) {
            processingEnvironment.printThenThrowError(
                "Authenticated and Unauthenticated Annotations cannot be used together."
            )
        }
    }

    private fun validateFunctionReceiver(memberName: MemberName) {
        when (memberName) {
            !in VALID_EXTENSION_CLASSES -> {
                val extensionClasses = VALID_EXTENSION_CLASSES.joinToString(", ") { it.canonicalName }
                processingEnvironment.printThenThrowError("Only [$extensionClasses] are supported as extension" +
                        " receivers for request methods. Found $this for route $fullRoutePath")
            }
        }
    }

    override fun compareTo(other: RequestFunction): Int {
        val comparison = fullRoutePath.compareTo(other.fullRoutePath)
        return if (comparison != 0) comparison else requestMethodSimpleName.compareTo(other.requestMethodSimpleName)
    }
}

private fun List<String>.asVarArgs(): String = this.let {
    return joinToString(", ") { "\"$it\"" }
}
