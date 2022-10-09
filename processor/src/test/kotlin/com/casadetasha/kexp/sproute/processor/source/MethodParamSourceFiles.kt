package com.casadetasha.kexp.sproute.processor.source

import com.tschuchort.compiletesting.SourceFile

object MethodParamSourceFiles {

    internal val methodParamSource = SourceFile.kotlin(
        "MethodParamSource.kt", """
            package com.casadetasha
            
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.PathParam
            import com.casadetasha.kexp.sproute.annotations.Sproute
            
            @Sproute("params/route")
            class Route {
                @Get("/get-a-route/{param}")
                fun get(@PathParam param: String) = "get me"
            }
            
            @Sproute("params/route/fun-get/{other-param}")
            @Get
            fun get(@PathParam("other-param") pathParam: String) = "Wahaaaa"

        """.trimIndent()
    )
}
