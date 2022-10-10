package com.casadetasha.kexp.sproute.processor.values

import com.casadetasha.kexp.annotationparser.AnnotationParser.printThenThrowError
import com.casadetasha.kexp.annotationparser.KotlinParameter
import com.casadetasha.kexp.sproute.annotations.PathParam
import com.casadetasha.kexp.sproute.annotations.QueryParam
import com.casadetasha.kexp.sproute.processor.ktx.asCanonicalName
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.MethodNames.applicationCallGetter
import com.casadetasha.kexp.sproute.processor.values.KotlinNames.MethodNames.applicationGetter
import com.squareup.kotlinpoet.MemberName
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.metadata.KmValueParameter
import kotlin.reflect.KClass

internal object KotlinNames {

    val VALID_EXTENSION_CLASSES =
        listOf(
            ApplicationCall::class.toMemberName(),
            Route::class.toMemberName()
        )

    private object KtorMethodNames {
        // I'm not sure how to get the name of an overloaded generic method via reflection
        const val RESPOND = "respond"
        const val ROUTE: String = "route"
        val ROUTING: String = Application::routing.name
        val AUTH: String = Route::authenticate.name
        val CALL: String = PipelineContext<*, ApplicationCall>::call.name
        val APPLICATION = PipelineContext<*, ApplicationCall>::application.name
        val APPLY: String = KClass<*>::apply.name
    }

    object GeneratedMethodNames {
        const val SPROUTE_CONFIGURATION: String = "configureSproutes"
    }

    object KtorPackageNames {
        const val APPLICATION = "io.ktor.server.application"
        const val AUTH = "io.ktor.server.auth"
        const val RESPONSE = "io.ktor.server.response"
        const val ROUTING = "io.ktor.server.routing"
        const val KOTLIN = "kotlin"
    }

    object MethodNames {
        val applicationGetter = MemberName(KtorPackageNames.APPLICATION, KtorMethodNames.APPLICATION)
        val applicationCallGetter = MemberName(KtorPackageNames.APPLICATION, KtorMethodNames.CALL)
        val authenticationScopeMethod = MemberName(KtorPackageNames.AUTH, KtorMethodNames.AUTH)
        val routingMethod = MemberName(KtorPackageNames.ROUTING, KtorMethodNames.ROUTING)
        val routeMethod = MemberName(KtorPackageNames.ROUTING, KtorMethodNames.ROUTE)
        val callRespondMethod = MemberName(KtorPackageNames.RESPONSE, KtorMethodNames.RESPOND)
        val applyMethod = MemberName(KtorPackageNames.KOTLIN, KtorMethodNames.APPLY)
    }

    private val validParamMemberMap = mapOf(
        Application::class.asCanonicalName() to SprouteMemberParameter(applicationGetter),
        ApplicationCall::class.asCanonicalName() to SprouteMemberParameter(applicationCallGetter)
    )

    private val validParameterTypes = validParamMemberMap.keys

    fun Collection<KotlinParameter>.toSprouteParameters(): List<SprouteParameter> =
        apply {
            val containsInvalidParameter = any { !it.isValidSprouteParameter() }
            if (containsInvalidParameter) printThenThrowError(
                "Only Strings annotated with @PathParam and the following parameters can be used for Route Classes" +
                        " and Request methods: ${validParameterTypes.joinToString()}. Attempted to send parameters: " +
                        " ${this@apply.joinToString(","){ it.kmParameter.name }}"
            )
        }
            .map { it.toSprouteParam() }

    private fun KotlinParameter.isValidSprouteParameter(): Boolean {
        val canonicalName = kmParameter.asCanonicalName()
        return canonicalName !in validParameterTypes || !isPathSprouteParameter() || !isQuerySprouteParameter()
    }

    private fun KotlinParameter.isPathSprouteParameter(): Boolean {
        val canonicalName = kmParameter.asCanonicalName()
        val hasPathParamAnnotation = element.getAnnotation(PathParam::class.java) != null
        return hasPathParamAnnotation && canonicalName == String::class.asCanonicalName()
    }

    private fun KotlinParameter.isQuerySprouteParameter(): Boolean {
        val canonicalName = kmParameter.asCanonicalName()
        val hasQueryParamAnnotation = element.getAnnotation(QueryParam::class.java) != null
        return hasQueryParamAnnotation && canonicalName == String::class.asCanonicalName()
    }

    private fun KotlinParameter.toSprouteParam(): SprouteParameter {
        val isQueryParam = isQuerySprouteParameter()
        val isPathParam = isPathSprouteParameter()

        if(isQueryParam && isPathParam) {
            printThenThrowError("Parameter ${kmParameter.name} is annotated as both a QueryParam and a" +
                    " PathParam. Parameters cannot be both.")
        }

        if (isPathParam) {
            val userSetKey = element.getAnnotation(PathParam::class.java)!!.paramKey
            val paramKey = userSetKey.ifBlank { kmParameter.name }
            return SproutePathParamParameter(paramKey = paramKey)
        }

        if (isQueryParam) {
            val userSetKey = element.getAnnotation(QueryParam::class.java)!!.paramKey
            val paramKey = userSetKey.ifBlank { kmParameter.name }
            return SprouteQueryParamParameter(paramKey = paramKey)
        }

        return validParamMemberMap[kmParameter.asCanonicalName()]!!
    }
}
