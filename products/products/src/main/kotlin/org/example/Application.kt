package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.routers.configureProductsRouting
import org.example.routers.configureRecipeRouting
import org.example.routers.configureMealRouting

fun main() {
    embeddedServer(Netty, port = 5022, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@Suppress("unused")
fun Application.module() {
    configureProductsRouting()
    configureRecipeRouting()
    configureMealRouting()
}
