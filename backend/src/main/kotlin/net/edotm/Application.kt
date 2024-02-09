package net.edotm

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.edotm.plugins.*
import java.util.*

val sessionInvalidationTimer = Timer()

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureContentNegotiation()
    configureWebSockets()
    configureSessions()
    configureHTTP()
    configureRouting()

    sessionInvalidationTimer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            Sessions.invalidateExpiredSessions()
            Rooms.removeEmptyRooms()
        }
    }, 0, 3600000)
}
