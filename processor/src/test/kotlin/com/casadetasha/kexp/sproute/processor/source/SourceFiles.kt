package com.casadetasha.kexp.sproute.processor.source

import com.tschuchort.compiletesting.SourceFile

object SourceFiles {
    internal val boringRouteSource = SourceFile.kotlin(
        "BoringRouteSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute

            @Sproute("/route")
            class Route {
                @Get("/get-a-route")
                fun get() = "get me"
            }
        """.trimIndent()
    )

    internal val addedParameterRouteSource = SourceFile.kotlin(
        "AddedParameterRouteSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import io.ktor.server.application.*
            import io.ktor.server.response.respond

            @Sproute("/route")
            class ParameterRoutes(application: Application) {
                @Get("/get-a-string")
                fun getString() = "get me"

                @Get("/get-a-string-with-call")
                fun getStringWithCall(call: ApplicationCall) = "get me"

                @Get("/suspend-get-a-string-with-call")
                suspend fun suspendGetStringWithCall(call: ApplicationCall) = call.respond("get me")
            }
        """.trimIndent()
    )

    internal val functionSource = SourceFile.kotlin(
        "FunctionSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import io.ktor.server.application.*

            @Get("/function/route")
            fun functionGet() = "I'm a function!"
        """.trimIndent()
    )

    internal val clonedFunctionSource = SourceFile.kotlin(
        "ClonedFunctionSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import io.ktor.server.application.*

            @Get("/function/route")
            fun functionGet() = "I'm a function!"
        """.trimIndent()
    )

    internal val routeAndFunctionSource = SourceFile.kotlin(
        "RouteAndFunctionSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import io.ktor.server.application.*

            @Sproute("/route")
            class Route {
                @Get("/get-a-route")
                fun innerGet() = "get me"
            }

            @Get("/function/outer_route")
            fun outerGet() = "I'm a function!"
        """.trimIndent()
    )

    internal val multiFunctionSource = SourceFile.kotlin(
        "MultiFunctionSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Post

            @Get("/function/route")
            fun functionGet() = "I'm a get function!"

            @Post("/function/route")
            fun functionPost() = "I'm a post function!"
        """.trimIndent()
    )

    internal val routeExtensionFunctionSource = SourceFile.kotlin(
        "RouteExtensionFunctionSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import io.ktor.server.routing.*

            @Get("/function/route")
            fun Route.functionGet() = this.let { "I'm a get function!" }
        """.trimIndent()
    )

    internal val applicationCallExtensionFunctionSource = SourceFile.kotlin(
        "ApplicationCallExtensionFunctionSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import io.ktor.server.application.*
            import io.ktor.server.response.*

            @Get("/function/route")
            suspend fun ApplicationCall.functionGet() { respond("I'm a get function!") }
        """.trimIndent()
    )

    internal val returnValueApplicationCallExtensionFunctionSource = SourceFile.kotlin(
        "ReturnValueApplicationCallExtensionFunctionSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import io.ktor.server.application.*

            @Get("/function/route")
            fun ApplicationCall.functionGet() = this.let { "I'm a get function!" }
        """.trimIndent()
    )

    // KClass<*> is unused because it's only needed to test that compiling fails
    @Suppress("unused")
    internal val unrecognizedExtensionFunctionSource = SourceFile.kotlin(
        "Unrecognized ExtensionFunctionSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import kotlin.reflect.KClass

            @Get("/function/route")
            fun KClass<*>.functionGet() { "I'm a get function!" }
        """.trimIndent()
    )

    internal val duplicateRouteDifferentRequestSource = SourceFile.kotlin(
        "DuplicateRouteSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Post
            import kotlin.reflect.KClass

            @Get("/function/route")
            fun functionGet() { "I'm a get function!" }

            @Post("/function/route")
            fun duplicateFunctionGet() { "I'm a post function!" }
        """.trimIndent()
    )

    internal val duplicateRouteSource = SourceFile.kotlin(
        "DuplicateRouteSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import kotlin.reflect.KClass

            @Get("/function/route")
            fun functionGet() { "I'm a get function!" }

            @Get("/function/route")
            fun duplicateFunctionGet() { "I'm a get function!" }
        """.trimIndent()
    )

    internal val duplicatePathParamRouteSource = SourceFile.kotlin(
        "DuplicatePathParamRouteSource.kt", """
            package com.casadetasha

            import com.casadetasha.kexp.sproute.annotations.Get
            import kotlin.reflect.KClass

            @Get("/function/{param-name}/route")
            fun functionGet() { "I'm a get function!" }

            @Get("/function/{different-param-name}/route")
            fun duplicateFunctionGet() { "I'm a get function!" }
        """.trimIndent()
    )
}