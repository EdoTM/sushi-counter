package net.edotm

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.edotm.plugins.configureHTTP
import net.edotm.plugins.configureRouting
import net.edotm.plugins.configureSessions
import net.edotm.plugins.configureWebSockets

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureWebSockets()
    configureSessions()
    configureHTTP()
    configureRouting()
}
