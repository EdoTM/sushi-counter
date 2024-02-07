package net.edotm.plugins

import io.ktor.server.application.*
import io.ktor.server.websocket.*

fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriodMillis = 15000
        timeoutMillis = 15000
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}