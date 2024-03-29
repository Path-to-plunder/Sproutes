# Sproutes

## Overview

Sproutes are annotated endpoints that allow you to quickly build scalable APIs in Ktor.

This means:
* No manually writing route methods
* Hierarcichal routing (e.g. a "/customer" route could have a child "/orders" route)
* Authentication that passes to child routes
* A generated map of all routes

Sproutes manages this by converting an annotation shorthand into Ktor routes.

## Setup
Add the annotations and kapt processor to your gradle dependencies of a ktor project:

```kts
implementation("com.casadetasha:sproutes:2.1.2-beta-1")
kapt("com.casadetasha:sproutes-processor:2.1.2-beta-1")
```

(For instructions on setting up kapt, see https://kotlinlang.org/docs/kapt.html#using-in-gradle)

Add the generated configuration method to your `Application` setup. When you build your project with the kapt processor, this method will be generated by the kapt processor and will add the routing structure that calls your annotated methods.

###  Embedded Engine server setup
```kt
Application.kt

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureSproutes()
    }.start(wait = true)
}
```

### EngineMain server setup

```kt
Application.kt

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    configureSproutes()
}
```

## Comparison to vanilla Ktor

### Example routing from Ktor docs
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
        call.respond("Found customer")
    }
}

fun Route.createCustomerRoute() {
    post("/customer") {
        call.respond("Created customer")
    }
}
```

### Example with Sproute functions
We can accomplish the same by creating a couple of annotated functions marked as request methods.

```kt
CustomerSproute.kt

@Get("/customer/{id}")
fun getCustomer() = "Found customer"

@Post("/customer")
fun createCustomer() = "Created customer"
```

### How it works
When the kapt plugin runs, it generates the `configureSproutes()` method that the setup instructions above say to place in your Application setup method. This method is generated from the annotated classes and methods.

```KT
Sproutes.kt GENERATED | Functions

public fun Application.configureSproutes(): Unit {
  routing {
    route("/customer") `/customer`@ {
      post { call.respond( createCustomer() ) }

      route("/{id}") `/customer/{id}`@ {
        `get` { call.respond( getCustomer() ) }
      }
    }
  }
}
```

## Sproutes
We can DRY up multiple request methods by creating a `@Sproute` class that will hold the methods.

```kt
CustomerSproute.kt

@Sproute("/customer")
class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}
```

```KT
Sproutes.kt GENERATED | Class

public fun Application.configureSproutes(): Unit {
  routing {
    route("/customer") `/customer`@ {
      post { call.respond( CustomerSproute().createCustomer() ) }

      route("/{id}") `/customer/{id}`@ {
        `get` { call.respond( CustomerSproute().getCustomer() ) }
      }
    }
  }
}
```


### Sproute parameters
In the example above we've DRY'd up the "/customer" route for our `GET` and `POST` methods, but even with that avoiding typos in the route name this is arguably worse for the added bloated. So why would we want to use this?

The primary usefulness comes when we have shared code between methods. Let's take an example where we have a CustomerStore that we get from Kodein DI.

#### With path parameters

```kt
CustomerSproute.kt

@Sproute("/customer")
class CustomerSproute(application: Application) {
    private val customerStore by application.closestDI().instance<CustomerStore>()

    @Get("/{id}")
    suspend fun getCustomer(@PathParam id: String): String {
        val customer = customerStore.get(id)
        return "Found customer $customer"
    }

    @Post
    fun createCustomer() = "Created customer ${customerStore.create()}"
}
```

#### With query parameters

```kt
CustomerSproute.kt

@Sproute("/customer")
class CustomerSproute(application: Application) {
    private val customerStore by application.closestDI().instance<CustomerStore>()

    @Get
    suspend fun getCustomer(@QueryParam id: String?): String {
        val customer = customerStore.get(id!!)
        return "Found customer $customer"
    }

    @Post
    fun createCustomer() = "Created customer ${customerStore.create()}"
}
```

We've now created a request logic class that has everything it needs passed into it from our generated routing. The routing itself stays readable as well, as you can see in the generated output from the @PathParam sample below:

```kt
Sproutes.kt GENERATED

routing {
  route("/customer") `/customer`@ {
    post { call.respond( CustomerSproute(application).createCustomer() ) }

    route("/{id}") `/customer/{id}`@ {
      `get` { call.respond( CustomerSproute(application).getCustomer(call.parameters["id"]!!) ) }
    }
  }
}
```

Note that both `@PathParam` and `@QueryParam` will look for a parameter matching the variable name. You can override this by manually passing in a name.

```kt
@Sproute("/customer")
class CustomerSproute(application: Application) {
private val customerStore by application.closestDI().instance<CustomerStore>()

    @Get
    suspend fun getCustomer(@QueryParam("id") queryId: String?): String {
    }
}
```



## SprouteRoots
`Sproutes` that are a path extension of `Sproute` can attach themselves by setting a `sprouteRoot`.

```kt
CustomerSproute.kt

@Sproute("/customer")
class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}
```

```kt
Orders.kt

@Sproute("/orders", sprouteRoot = CustomerSproute::class)
class CustomerOrdersSproute {

    @Get("/{id}")
    fun findOrder() = "Searching for your order"

    @Post
    fun placeOrder() = "Created order"
}
```

### Authenticated SprouteRoots
Sproutes provide an `Authenticated` annotation to allow you to mark your `Sproutes` as requiring authentication. This status gets passed along with `SprouteRoots`

```kt
CustomerSproute.kt

@Sproute("/customer")
@Authenticated("oauth-google")
class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}
```

```kt
Orders.kt

@Sproute("/orders", sprouteRoot = CustomerSproute::class)
class CustomerOrdersSproute {

    @Get
    fun findOrder() = "Searching for your order"

    @Post
    fun placeOrder() = "Created order"
}
```

#### Generated Output
```KT
routing {
  authenticate("oauth-google")  {
    route("/customer") `/customer`@ {
      post { call.respond( CustomerSproute().createCustomer() ) }

      route("/orders") `/customer/orders`@ {
        `get` { call.respond( CustomerOrdersSproute().findOrder() ) }

        post { call.respond( CustomerOrdersSproute().placeOrder() ) }
      }

      route("/{id}") `/customer/{id}`@ {
        `get` { call.respond( CustomerSproute().getCustomer() ) }
      }
    }
  }
}
```


## API with examples

### SprouteRoot
Sproutes can supply a sprouteRoot (any other class annotated with `Sproute`) to chain off of it. This allows you to ensure that `Sproutes` that logically follow each other will always share the same root path.

```kt
CustomerSproute.kt

@Sproute("/customer")
private interface CustomerSproute

@Get("/{id}")
@Sproute(sprouteRoot = CustomerSproute::class)
fun getCustomer() = "Found customer"

@Post
@Sproute(sprouteRoot = CustomerSproute::class)
fun createCustomer() = "Created customer"
```

#### Generated routing

```kt
routing {
  route("/customer") `/customer`@ {
    post { call.respond( createCustomer() ) }

    route("/{id}") `/customer/{id}`@ {
      `get` { call.respond( getCustomer() ) }
    }
  }
}
```

---

### @SproutePackageRoot
`SproutePackageRoots` will append all package segments following its own. In other words, if you want to have a root package and let the subpackages count as a path prefix, put a `SproutePackageRoot` in the package that you want to be the root.

```kt
ProjectSprouteRoot.kt

@SproutePackageRoot
private interface ProjectSprouteRoot
```

```kt
api/v1/CustomerSproute.kt

@Sproute("/customer", sprouteRoot = ProjectSprouteRoot::class)
class CustomerSproute {

    @Get("/{id}")
    suspend fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}
```

#### Generated routing

```kt
routing {
  route("/api/v1/customer") `/api/v1/customer`@ {
    post { call.respond( CustomerSproute().createCustomer() ) }

    route("/{id}") `/api/v1/customer/{id}`@ {
      `get` { call.respond( CustomerSproute().getCustomer() ) }
    }
  }
}
```

---

### Nested SprouteRoots
Sproute roots can be more than one level deep.

```kt
CustomerSproute.kt

@Sproute("/api/v1")
private interface ApiV1Root

@Sproute("/customer", sprouteRoot = ApiV1Root::class)
class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}

@Get("/orders")
@Sproute(sprouteRoot = CustomerSproute::class)
fun getOrders() = "Getting orders"
```

#### Generated routing

```kt
routing {
  route("/api/v1/customer") `/api/v1/customer`@ {
    post { call.respond( CustomerSproute().createCustomer() ) }

    route("/orders") `/api/v1/customer/orders`@ {
      `get` { call.respond( getOrders() ) }
    }

    route("/{id}") `/api/v1/customer/{id}`@ {
      `get` { call.respond( CustomerSproute().getCustomer() ) }
    }
  }
}
```

---

### @Authenticated
Authentication can be set on a Sproute or Request method via the `@Authenticated` annotation.

### Unnamed authentication

```kt
CustomerSproute.kt

@Sproute("/customer")
@Authenticated
class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}
```

#### Generated routing

```kt
routing {
  authenticate()  {
    route("/customer") `/customer`@ {
      post { call.respond( CustomerSproute().createCustomer() ) }

      route("/{id}") `/customer/{id}`@ {
        `get` { call.respond( CustomerSproute().getCustomer() ) }
      }
    }
  }
}
```

---

### Optional authentication

```kt
CustomerSproute.kt

@Sproute("/customer")
class CustomerSproute {

    @Get("/{id}")
    @Authenticated(optional = true)
    fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}

```

#### Generated routing

```kt
routing {
  route("/customer") `/customer`@ {
    post { call.respond( CustomerSproute().createCustomer() ) }
  }

  authenticate(optional = true)  {
    route("/customer/{id}") `/customer/{id}`@ {
      `get` { call.respond( CustomerSproute().getCustomer() ) }
    }
  }
}
```
---
### Named authentication

```kt
CustomerSproute.kt

@Sproute("/customer")
@Authenticated("oauth-google")
class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}
```

#### Generated routing

```kt
routing {
  authenticate("oauth-google")  {
    route("/customer") `/customer`@ {
      post { call.respond( CustomerSproute().createCustomer() ) }

      route("/{id}") `/customer/{id}`@ {
        `get` { call.respond( CustomerSproute().getCustomer() ) }
      }
    }
  }
}
```
---
### Multi-named authentication

```kt
CustomerSproute.kt

@Sproute("/customer")
@Authenticated("oauth-google", "oauth-facebook")
class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Found customer"

    @Post
    fun createCustomer() = "Created customer"
}
```

#### Generated routing

```kt
routing {
  authenticate("oauth-google", "oauth-facebook")  {
    route("/customer") `/customer`@ {
      post { call.respond( CustomerSproute().createCustomer() ) }

      route("/{id}") `/customer/{id}`@ {
        `get` { call.respond( CustomerSproute().getCustomer() ) }
      }
    }
  }
}
```
---
### Nested Authentication
If a subpath should be exempted from Authenticated, @Unauthenticated can be used to remove it. Note that marking a Sproute as `@Unauthenticated` will pass down the unauthenticated status to child Sproutes.

```kt
CustomerSproute.kt

@Sproute("/api/v1")
@Authenticated
private interface ApiV1Root

@Sproute("/customer", sprouteRoot = ApiV1Root::class)
class CustomerSproute {

    @Get("/{id}")
    fun getCustomer() = "Found customer"

    @Post
    @Unauthenticated
    fun createCustomer() = "Created customer"
}
```

#### Generated routing

```kt
routing {
  route("/api/v1/customer") `/api/v1/customer`@ {
    post { call.respond( CustomerSproute().createCustomer() ) }
  }

  authenticate()  {
    route("/api/v1/customer/{id}") `/api/v1/customer/{id}`@ {
      `get` { call.respond( CustomerSproute().getCustomer() ) }
    }
  }
}
```

---

### Complex Nested Sproutes
This example demonstrates how Sproutes merges nested routes with overlapping routes that have overlapping paths and authentication.

```kt
ProjectSprouteRoot.kt

@SproutePackageRoot
@Authenticated("oauth-google")
private interface ProjectSprouteRoot
```

```kt
api/v1/CustomerSproute.kt

@Sproute("/customer", sprouteRoot = ProjectSprouteRoot::class)
class CustomerSproute {

    @Get("/{id}")
    suspend fun getCustomer(call: ApplicationCall) = "Found customer"

    @Put
    suspend fun updateCustomer() = "Updated Customer"

    @Post
    fun createCustomer() = "Created Customer"
}

@Delete("/api/v1/customer")
@Authenticated("oauth-google")
suspend fun ApplicationCall.deleteCustomer() { respond("Deleted customer") }

@Get
@Sproute("/orders", sprouteRoot = CustomerSproute::class)
fun getOrders() = "Getting orders"
```

#### Generated routing

```kt
routing {
  authenticate("oauth-google")  {
    route("/api/v1/customer") `/api/v1/customer`@ {
      delete { call.apply { deleteCustomer(application) } }

      post { call.respond( CustomerSproute(application).createCustomer() ) }

      put { CustomerSproute(application).updateCustomer(call) }

      route("/orders") `/api/v1/customer/orders`@ {
        `get` { call.respond( getOrders() ) }
      }

      route("/{id}") `/api/v1/customer/{id}`@ {
        `get` { CustomerSproute(application).getCustomer(call) }
      }
    }
  }
}
```

---

### ApplicationCall Sproutes
#### ApplicationCall extension Sproutes
Making the method an `ApplicationCall` extension method will allow you to create a method nested inside the call.

```kt
CustomerSproute.kt

@Get("/customer/{id}")
suspend fun ApplicationCall.getCustomer() = respond("Found customer ${parameters["id"]}")

@Post("/customer")
fun createCustomer() = "Created customer"
```

#### Generated ConfigureSproutes() Method

```kt
routing {
  route("/customer") `/customer`@ {
    post { call.respond( createCustomer() ) }

    route("/{id}") `/customer/{id}`@ {
      `get` { call.apply { getCustomer() } }
    }
  }
}

```

---

#### ApplicationCall method parameter Sproutes
You can add the `ApplicationCall` as a parameter as well, which can be useful in situations where you cannot make the request an extension method.

```kt
CustomerSproute.kt

@Sproute("/customer")
class CustomerSproute {

    @Get("/{id}")
    suspend fun getCustomer(call: ApplicationCall) = call.respond("Found customer")

    @Post
    fun createCustomer() = "Created customer"
}
```

#### Generated ConfigureSproutes() Method

```kt
routing {
  route("/customer") `/customer`@ {
    post { call.respond( CustomerSproute().createCustomer() ) }

    route("/{id}") `/customer/{id}`@ {
      `get` { CustomerSproute().getCustomer(call) }
    }
  }
}
```

---

### Sproute with Application Class Parameter (using Kodein DI to show usecase)
Even though you can get the `Application` from the `ApplicationCall`, it can be useful for readablity to have it available as a variable directly. In order to avoid creating a worthless example, I've included a small snippet using `Kodein` to demonstrate a real world example of this being useful.

```kt
CustomerSproute.kt

@Sproute("/customer")
class CustomerSproute(application: Application) {
    private val customerStore by application.closestDI().instance<CustomerStore>()

    @Get("/{id}")
    suspend fun getCustomer(call: ApplicationCall) {
        val customer = customerStore.get(call.parameters["id"])
        call.respond("Found customer $customer")
    }

    @Post
    fun createCustomer() = "Created customer ${customerStore.create()}"
}
```

#### Generated ConfigureSproutes() Method

```kt
routing {
  route("/customer") `/customer`@ {
    post { call.respond( CustomerSproute(application).createCustomer() ) }

    route("/{id}") `/customer/{id}`@ {
      `get` { CustomerSproute(application).getCustomer(call) }
    }
  }
}
```
