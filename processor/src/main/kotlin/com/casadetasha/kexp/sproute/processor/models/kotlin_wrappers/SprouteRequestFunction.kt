package com.casadetasha.kexp.sproute.processor.models.kotlin_wrappers

import com.casadetasha.kexp.annotationparser.KotlinValue.KotlinFunction
import com.casadetasha.kexp.sproute.processor.SprouteAnnotationProcessor.Companion.processingEnvironment
import com.casadetasha.kexp.sproute.processor.ktx.printThenThrowError
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.models.Root
import com.casadetasha.kexp.sproute.processor.models.objects.KotlinNames
import com.casadetasha.kexp.sproute.processor.models.objects.KotlinNames.VALID_EXTENSION_CLASSES
import com.casadetasha.kexp.sproute.processor.models.objects.KotlinNames.toRequestParamMemberNames
import com.casadetasha.kexp.sproute.processor.models.objects.SprouteRequestAnnotations.getInstaRequestAnnotation
import com.casadetasha.kexp.sproute.processor.models.objects.SprouteRequestAnnotations.getRequestMethodName
import com.casadetasha.kexp.sproute.processor.models.objects.SprouteRequestAnnotations.getRouteSegment
import com.casadetasha.kexp.sproute.processor.models.objects.SprouteRequestAnnotations.shouldIncludeClassRouteSegment
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.ktor.application.*

@OptIn(KotlinPoetMetadataPreview::class)
internal class SprouteRequestFunction(
    override val sprouteAuthentication: SprouteAuthentication,
    private val sprouteRoot: Root,
    kotlinFunction: KotlinFunction,
    classRouteSegment: String
): Root, Comparable<SprouteRequestFunction> {

    private val baseRoutePath: String by lazy {
        val includeClassRouteSegment: Boolean = shouldIncludeClassRouteSegment(requestAnnotation)
        val usableClassSegment: String = if (includeClassRouteSegment) classRouteSegment else ""
        sprouteRoot.getSproutePathForPackage(kotlinFunction.packageName) + usableClassSegment
    }

    private val functionPathSegment: String by lazy { getRouteSegment(requestAnnotation).removeSuffix("/") }
    val fullRoutePath: String by lazy { baseRoutePath + functionPathSegment }

    val simpleName: String = kotlinFunction.simpleName
    val memberName: MemberName = kotlinFunction.memberName
    val params: List<MemberName> = kotlinFunction.parameters.toRequestParamMemberNames()
    val receiver: MemberName? = kotlinFunction.receiver.apply { validateFunctionReceiver(this) }

    val hasReturnValue: Boolean = kotlinFunction.hasReturnValue.apply { validateReturnValue(this) }
    val isApplicationCallExtensionMethod: Boolean = receiver == ApplicationCall::class.toMemberName()

    private val requestAnnotation: Annotation = kotlinFunction.element.getInstaRequestAnnotation()
    private val requestMethodSimpleName: String = getRequestMethodName(requestAnnotation)
    val requestMethodName: MemberName = MemberName(KotlinNames.KtorPackageNames.ROUTING, requestMethodSimpleName)

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

    // This should always use a character that would be illegal in Kotlin class names to prevent clashes
    override val key: String = "${kotlinFunction.packageName}/${kotlinFunction.simpleName}"

    override fun getSproutePathForPackage(sproutePackage: String): String {
        return fullRoutePath
    }

    override fun compareTo(other: SprouteRequestFunction): Int {
        val comparison = fullRoutePath.compareTo(other.fullRoutePath)
        return if (comparison != 0) comparison else requestMethodSimpleName.compareTo(other.requestMethodSimpleName)
    }
}
