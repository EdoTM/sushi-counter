package net.edotm.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import net.edotm.UserSession

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("USER_SESSION", SessionStorageMemory()) {
            cookie.maxAgeInSeconds = 3600 * 3
        }
    }
}