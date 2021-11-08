package com.casadetasha.kexp.sproute.processor.source

import com.tschuchort.compiletesting.SourceFile

object RootedSourceFiles {

    internal val singleRootedSource = SourceFile.kotlin(
        "SingleRootedSource.kt", """
            package com.casadetasha
            
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import io.ktor.application.*
            import io.ktor.response.respond
            
            @Sproute("/root")
            internal interface MyRoot
            
            @Sproute("/outlet", MyRoot::class)
            class Outlet {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
        """.trimIndent()
    )

    internal val packageRootedSource = SourceFile.kotlin(
        "PackageRootedSource.kt", """
            package com.casadetasha
            
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import com.casadetasha.kexp.sproute.annotations.SproutePackageRoot
            import io.ktor.application.*
            import io.ktor.response.respond
            
            @SproutePackageRoot("/package_root")
            internal interface MyRoot
            
            @Sproute("/outlet", MyRoot::class)
            class Outlet {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
        """.trimIndent()
    )

    internal val multiRootedSource = SourceFile.kotlin(
        "MultiRootedSource.kt", """
            package com.casadetasha
            
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import io.ktor.application.*
            import io.ktor.response.respond
            
            @Sproute("/root")
            internal interface MyRoot
            
            @Sproute("/chain", MyRoot::class)
            class ChainRoot {
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
            
            @Sproute("/outlet", ChainRoot::class)
            class Outlet {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
        """.trimIndent()
    )

    internal val cyclicalRootSource = SourceFile.kotlin(
        "CyclicalRootSource.kt", """
            package com.casadetasha
            
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import io.ktor.application.*
            import io.ktor.response.respond
            
            @Sproute("/first_chain", SecondChain::class)
            class FirstChain {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
            
            @Sproute("/second_chain", FirstChain::class)
            class SecondChain {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
        """.trimIndent()
    )

    internal val cyclicalRootWithGapSource = SourceFile.kotlin(
        "CyclicalRootSource.kt", """
            package com.casadetasha
            
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import io.ktor.application.*
            import io.ktor.response.respond
            
            @Sproute("/first_chain", ThirdChain::class)
            class FirstChain {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
            
            @Sproute("/second_chain", FirstChain::class)
            class SecondChain {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
            
            @Sproute("/third_chain", SecondChain::class)
            class ThirdChain {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
        """.trimIndent()
    )

    internal val selfReferentialRootSource = SourceFile.kotlin(
        "SelfReferentialRootSource.kt", """
            package com.casadetasha
            
            import com.casadetasha.kexp.sproute.annotations.Get
            import com.casadetasha.kexp.sproute.annotations.Sproute
            import io.ktor.application.*
            import io.ktor.response.respond
            
            @Sproute("/infinite_chain", InfiniteChain::class)
            class InfiniteChain {
                @Get
                suspend fun get(call: ApplicationCall) = call.respond("get me")
            }
        """.trimIndent()
    )
}
