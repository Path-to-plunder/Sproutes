package com.casadetasha.kexp.sproute.processor.post.routes

import com.casadetasha.kexp.sproute.annotations.*

@Sproute("/param_route/{param}")
class PathParamRequestRoutes(@PathParam val param: String) {
    @Get("/{get-param}")
    fun get(@PathParam("get-param") getParam: String): String {
        return "Path parammed GET: $param | $getParam."
    }
}


@Get("param_route/query_param_test")
fun getWithQueryParams(@QueryParam("renamed-param") renamedParam : String?, @QueryParam param: String?): String {
    return "Query parammed GET: $renamedParam | $param."
}
