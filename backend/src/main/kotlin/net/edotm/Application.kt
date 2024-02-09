package net.edotm

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.edotm.plugins.*
import java.util.*

val sessionInvalidationTimer = Timer()

val SECRET_SIGN_KEY: String? = System.getenv("SECRET_SIGN_KEY")
val CLEANUP_CHECK_INTERVAL_SECONDS = System.getenv("CLEANUP_CHECK_INTERVAL_SECONDS")?.toLong() ?: 600
val SESSION_EXPIRATION_SECONDS = System.getenv("SESSION_EXPIRATION_SECONDS")?.toLong() ?: 3600
val ROOM_EXPIRATION_SECONDS = System.getenv("ROOM_EXPIRATION_SECONDS")?.toLong() ?: SESSION_EXPIRATION_SECONDS
val MAX_ROOMS_PER_ADDRESS = System.getenv("MAX_ROOMS_PER_ADDRESS")?.toInt() ?: 3

fun main() {
    if (SECRET_SIGN_KEY == null) {
        throw IllegalArgumentException("SECRET_SIGN_KEY environment variable is not set")
    }
    Sessions.sessionExpirationMillis = SESSION_EXPIRATION_SECONDS * 1000
    Rooms.roomExpirationMillis = ROOM_EXPIRATION_SECONDS * 1000
    Rooms.maxRoomsPerAddress = MAX_ROOMS_PER_ADDRESS
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module, watchPaths = listOf("classes"))
        .start(wait = true)
}

fun Application.module() {
    configureContentNegotiation()
    configureWebSockets()
    configureSessions(SECRET_SIGN_KEY!!)
    configureHTTP()
    configureRouting()

    sessionInvalidationTimer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            Sessions.invalidateExpiredSessions()
            Rooms.cleanupEmptyRooms()
        }
    }, 0, CLEANUP_CHECK_INTERVAL_SECONDS * 1000)
}
