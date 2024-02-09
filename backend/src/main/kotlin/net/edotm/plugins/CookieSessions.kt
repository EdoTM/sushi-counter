package net.edotm.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import net.edotm.UserSession

fun Application.configureSessions(signingKey: String) {
    install(Sessions) {
        val signingKeyHex = hex(signingKey)
        cookie<UserSession>("USER_SESSION", SessionStorageMemory()) {
            cookie.maxAgeInSeconds = 3600 * 3
            cookie.extensions["SameSite"] = "lax"
            transform(SessionTransportTransformerMessageAuthentication(signingKeyHex))
        }
    }
}