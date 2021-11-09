# Iceburg Corn Sproutes

## Overview

I built Sproutes as a way to build scalable APIs in Ktor quickly. It allows me to break up my routing into multiple
files without losing the benefits of hierarchical routing, and without having to manage route configuration methods.
The idea is to build sproutes off of each other, e.g. an OrderSproute may have its root on CustomerSproute, inheriting
the authentication and path of the CustomerSproute as its base.

## Base Ktor Comparison

### Example from Ktor Docs for routing
(https://ktor.io/docs/structuring-applications.html#grouping-routing-definitions)

```kt
CustomerSproute.kt


fun Application.customerRoutes() {
    routing {
        customerByIdRoute()
        createCustomerRoute()
    }
}

fun Route.customerByIdRoute() {
    get("/customer/{id}") {
        call.respond("Got customer")
    }
}

fun Route.createCustomerRoute() {
    post("/customer") {
        call.respond("Posted customer")
    }
}
```

#### Application

```kt
Application.kt


fun main(args: Array<String>) = EngineMain.main(args)

fun Application.applicationModule() {
    customerRoutes()
}
```


### Sproute Shorthand

#### With Sproute functions

```kt
CustomerSproute.kt


@Get("/customer/{id}")
fun getCustomer() = "Got customer"

@Post("/customer")
fun postCustomer() = "Posted customer"
```

#### Application

```kt
Application.kt


fun main(args: Array<String>) = EngineMain.main(args)

fun Application.applicationModule() {
    configureSproutes()
}
```

### Alternate Sproute Shorthand

#### With Sproute class

```kt
CustomerSproute.kt


@Sproute("/customer")
internal class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Got customer"

    @Post
    fun postCustomer() = "Posted customer"
}
```

## Sample Complex Sproute

### Sample Sproute Code
This is a bloated example of different ways that you can use Sproutes and how they get merged together in the generated sproute configuration method.

```kt
ProjectSprouteRoot.kt


@SproutePackageRoot
@Authenticated("oauth-google")
private interface ProjectSprouteRoot
```

```kt
api/v1/CustomerSproute.kt


@Sproute("/customer", sprouteRoot = ProjectSprouteRoot::class)
internal class CustomerSproute(val application: Application) {
    private val customerStore by application.closestDI().instance<CustomerStore>()

    @Get("/{id}")
    suspend fun getCustomer(call: ApplicationCall) {
        val customer = customerStore.get(call.parameters["id"])
        call.respondText("Got customer ${customer}")
    }

    @Put
    suspend fun putCustomer(call: ApplicationCall) {
        val customer = call.receive<Customer>()
        call.respondText("Updated customer ${customerStore.update(customer)}")
    }

    @Post
    @Unauthenticated
    fun postCustomer() = "Posted customer ${customerStore.create()}"
}

@Delete("/api/v1/customer")
@Authenticated("oauth-google")
suspend fun ApplicationCall.deleteCustomer(application: Application) {
    val customerStore by application.closestDI().instance<CustomerStore>()
    respondText("Deleted customer ${customerStore.delete(receive<Customer>())}")
}

@Get
@Sproute("/orders", sprouteRoot = CustomerSproute::class)
fun getOrders() = "Getting orders"
```

### Generated ConfigureSproutes() Method
The goal of our generated output is to make a readable outline of all sproutes that can serve as an overview of all routing.

```kt
Sproutes.kt

public fun Application.configureSproutes(): Unit {
  routing {
    route("/api/v1/customer") `/api/v1/customer`@ {
      post { call.respond( CustomerSproute(application).postCustomer() ) }
    }

    authenticate("oauth-google")  {
      route("/api/v1/customer") `/api/v1/customer`@ {
        delete { call.apply { deleteCustomer(application) } }

        put { CustomerSproute(application).putCustomer(call) }

        route("/orders") `/api/v1/customer/orders`@ {
          `get` { call.respond( getOrders() ) }
        }

        route("/{id}") `/api/v1/customer/{id}`@ {
          `get` { CustomerSproute(application).getCustomer(call) }
        }
      }
    }
  }
}
```


## API with Examples

### Sproute with Root

```kt
CustomerSproute.kt


@Sproute("/customer")
private interface CustomerSproute

@Get("/{id}")
@Sproute(sprouteRoot = CustomerSproute::class)
fun getCustomer() = "Got customer"

@Post
@Sproute(sprouteRoot = CustomerSproute::class)
fun postCustomer() = "Posted customer"
```

### Nested Sproute roots

```kt
CustomerSproute.kt


@Sproute("/api/v1")
private interface ApiV1Root

@Sproute("/customer", sprouteRoot = ApiV1Root::class)
internal class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Got customer"

    @Post
    fun postCustomer() = "Posted customer"
}

@Get("/orders")
@Sproute(sprouteRoot = CustomerSproute::class)
fun getOrders() = "Getting orders"
```

### Authenticated Sproutes

```kt
CustomerSproute.kt


@Sproute("/customer")
@Authenticated
internal class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Got customer"

    @Post
    fun postCustomer() = "Posted customer"
}
```

### Nested Authentication Sproutes

```kt
CustomerSproute.kt


@Sproute("/api/v1")
@Authenticated
private interface ApiV1Root

@Sproute("/customer", sprouteRoot = ApiV1Root::class)
internal class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Got customer"

    @Post
    @Unauthenticated
    fun postCustomer() = "Posted customer"
}
```

### ApplicationCall Sproutes
Note: Though we are using `call.respondText` in this example, when a String value is returned as the function return value it will be sent via `call.respond`.


```kt
CustomerSproute.kt


@Sproute("/customer")
internal class CustomerSproute {

    @Get("/{id}")
    suspend fun getCustomer(call: ApplicationCall) = call.respondText("Got customer ${call.parameters["id"]}")

    @Post
    fun postCustomer() = "Posted customer"
}
```

### ApplicationCall extention Sproutes


```kt
CustomerSproute.kt


@Get("/customer/{id}")
suspend fun ApplicationCall.getCustomer() = respondText("Got customer ${parameters["id"]}")

@Post("/customer")
fun postCustomer() = "Posted customer"
```

### Sproute with Application Class Parameter using Kodein DI Example

```kt
CustomerSproute.kt

@Sproute("/customer")
internal class CustomerSproute(val application: Application) {
    private val customerStore by application.closestDI().instance<CustomerStore>()

    @Get("/{id}")
    suspend fun getCustomer(call: ApplicationCall) {
        val customer = customerStore.get(call.parameters["id"])
        respondText("Got customer $customer")
    }

    @Post
    fun postCustomer() = "Posted customer ${customerStore.create()}"
}
```

### Sproute with SproutePackageRoot
`SproutePackageRoot`s will append all package segments following its own. In other words, if you want to have a root package and let the subpackages count as a path prefix, put a `SproutePackageRoot` in the package that you want to be the root.

```kt
ProjectSprouteRoot.kt


@SproutePackageRoot
private interface ProjectSprouteRoot
```

```kt
api/v1/CustomerSproute.kt


@Sproute("/customer", sprouteRoot = ProjectSprouteRoot::class)
internal class CustomerSproute(val application: Application) {
    private val customerStore by application.closestDI().instance<CustomerStore>()

    @Get("/{id}")
    suspend fun getCustomer(call: ApplicationCall) {
        val customer = customerStore.get(call.parameters["id"])
        call.respondText("Got customer ${customer}")
    }

    @Post
    fun postCustomer() = "Posted customer ${customerStore.create()}"
}
```
