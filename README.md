# Iceburg Corn Sproutes

## Setup
Setup consists of adding a the generated configuration method to your `Application` setup.

### Embedded Engine Setup
```kt
Application.kt

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureSproutes()
    }.start(wait = true)
}
```

### EngineMain

```kt
Application.kt

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    configureSproutes()
}
```

## Overview

Sproutes were created as a way to build scalable APIs in Ktor quickly.

We wanted to break up our routing into multiple files without losing the benefits of hierarchical routing, and without having to manage route configuration methods. We wanted to write our http request methods, attach paths to them, run our server and have them route through.

We also wanted to make sure that routes could cascade off of each other, and that authentication set for a route would be applied by default to all subroutes. This can be done with stock Ktor, but it falls apart when routing becomes complicated enought to require multiple files.

The stretch goal was to generate a single route file that used Ktor's streamlined routing API to build a routing method that also serves to document our entire routing structure.

## Comparison to Stock Ktor

### Example routing from vanilla Ktor Docs
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

### Example with Sproute classes
We can also DRY it up by creating a `@Sproute` class that contains the methods (although this is arguably overkill for the example given).

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

### Generated code
The link is created by calling `configureSproutes()` in your Application setup. This method is generated from the annotated classes and methods, and is identical for both of the examples above.

```KT
Sproutes.kt GENERATED

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

In fact, the following sample generates an identical configuration method as well:


```KT
CustomerSproute.kt

@Sproute("/customer")
class CustomerSproute {
    @Post
    fun createCustomer() = "Created customer"
}
```

```KT
GetCustomer.kt

@Get("{id}")
@Sproute(sprouteRoot = CustomerSproute::class)
fun getCustomer() = "Found customer"
```

The last example is almost certainly not something you would do to add a single get method, but it does show one of the other features of Sproutes: SprouteRoots.

## API with Examples

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
If a subpath should be exempted from Authenticated, @Unauthenticated can be used to remove it. Note that this will not

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

### Sproute with Application Class Parameter using Kodein DI Example
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
