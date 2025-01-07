package net.edotm.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedheaders.*

fun Application.configureReverseProxyHeaders() {
    install(XForwardedHeaders)
}