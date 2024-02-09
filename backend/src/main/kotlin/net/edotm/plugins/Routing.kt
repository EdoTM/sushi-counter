package net.edotm.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.util.logging.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import net.edotm.Order
import net.edotm.Rooms
import net.edotm.Sessions
import net.edotm.UserData
import net.edotm.UserSession

val logger = KtorSimpleLogger("Routing")

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, world!\n")
        }

        webSocket("/order") {
            val userData: UserData
            try {
                userData = call.getSession()
            } catch (e: BadRequestException) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session present"))
                return@webSocket
            }
            val room = userData.room
            if (room == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No room"))
                return@webSocket
            }
            send("Connected to $room")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val command = frame.readText()
                when {
                    command.startsWith("order:") -> {
                        placeOrder(command.removePrefix("order:"), userData)
                        logger.info("Order placed by ${userData.sessionId}. Total orders: ${userData.orders}")
                        send("OK")
                    }

                    command == "close" -> {
                        Sessions.removeSession(userData.sessionId)
                        userData.clearRoomAndOrders()
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }

        get("/orders") {
            val userData = call.getSession()
            call.respond(userData.orders.associate { it.name to it.quantity })
        }

        put("/room") {
            val room = retrieveRoomFromRequest()
            val userData = call.getOrCreateSession()
            try {
                Rooms.createRoom(room, call.request.local.remoteAddress)
                addToRoom(userData, room)
                logger.info("User from ${call.request.local.remoteAddress} created room $room")
                call.respond(HttpStatusCode.Created)
            } catch (e: Rooms.RoomExistsException) {
                call.respond(HttpStatusCode.OK)
            }
        }

        post("/room/join") {
            val room = retrieveRoomFromRequest()
            val userData = call.getOrCreateSession()
            try {
                addToRoom(userData, room)
                logger.info("User joined room $room")
                call.respond(HttpStatusCode.OK)
            } catch (e: Rooms.RoomNotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        delete("/room") {
            val userData = call.getOrCreateSession()
            if (userData.room == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            try {
                Rooms.remove(userData.room!!)
                userData.clearRoomAndOrders()
                call.respond(HttpStatusCode.OK)
            } catch (e: Rooms.RoomNotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/room/total") {
            val userData = call.getSession()
            val room = userData.room
            if (room == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            try {
                val orders = Rooms.get(room).aggregateAndGetOrders()
                call.respond(orders)
            } catch (e: Rooms.RoomNotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun placeOrder(order: String, userData: UserData) {
    val tokens = order.split("/")
    if (tokens.size != 2) {
        throw IllegalArgumentException("Order should be in format '<item>x<qty>'")
    }
    val (item, quantity) = tokens
    val qty = quantity.toIntOrNull() ?: throw IllegalArgumentException("Quantity should be a number")
    userData.addOrder(Order(item, qty))
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveRoomFromRequest(): String {
    try {
        return normalize(call.receiveText())
    } catch (e: Exception) {
        throw BadRequestException("Invalid room name")
    }
}

private fun ApplicationCall.getSession(): UserData {
    val session = sessions.get<UserSession>()
    if (Sessions.hasSession(session?.id)) {
        return Sessions.get(session!!.id)
    } else {
        throw BadRequestException("No session")
    }
}

private fun ApplicationCall.getOrCreateSession(): UserData {
    try {
        return getSession()
    } catch (e: BadRequestException) {
        val newSessionId = Sessions.newSession()
        sessions.set(UserSession(newSessionId))
        return Sessions.get(newSessionId)
    }
}

private fun addToRoom(user: UserData, room: String) {
    user.clearRoomAndOrders()
    user.room = room
    Rooms.get(room).users.add(user)
}

private fun normalize(s: String): String {
    return s.trim().replace(" ", "").lowercase()
}