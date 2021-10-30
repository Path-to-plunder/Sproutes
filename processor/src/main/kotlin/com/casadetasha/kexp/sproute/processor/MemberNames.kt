package com.casadetasha.kexp.sproute.processor

import com.casadetasha.kexp.sproute.processor.MemberNames.MethodNames.applicationCallGetter
import com.casadetasha.kexp.sproute.processor.MemberNames.MethodNames.applicationGetter
import com.casadetasha.kexp.sproute.processor.ktx.asCanonicalName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.auth.*
import io.ktor.response.*
import kotlin.reflect.KClass

internal object MemberNames {

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

    object KtorPackageNames {
        const val APPLICATION = "io.ktor.application"
        const val AUTH = "io.ktor.auth"
        const val RESPONSE = "io.ktor.response"
        const val ROUTING = "io.ktor.routing"
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
        Application::class.asCanonicalName() to applicationGetter,
        ApplicationCall::class.asCanonicalName() to applicationCallGetter
    )
    private val validParameterTypes = validParamMemberMap.keys

    @OptIn(KotlinPoetMetadataPreview::class)
    fun List<ImmutableKmValueParameter>.toRequestParamMemberNames(): List<MemberName> {
        return map { it.asCanonicalName() }
            .apply {
                val containsInvalidParameter = any { it !in validParameterTypes }
                if (containsInvalidParameter) throw IllegalArgumentException(
                    "Only the following parameters can be used for Route Classes and Request methods:" +
                            " ${validParameterTypes.joinToString()}. Attempted to send parameters: " +
                            " ${this@apply.joinToString()}"
                )
            }
            .map { validParamMemberMap[it]!! }
    }
}