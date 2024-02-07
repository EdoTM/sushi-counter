package net.edotm.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import net.edotm.Order
import net.edotm.Rooms
import net.edotm.Sessions
import net.edotm.UserData
import net.edotm.UserSession

fun Application.configureRouting() {

    routing {
        webSocket("/order") {
            val session = call.getSession()
            val room = session.room
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
                        placeOrder(command.removePrefix("order:"), session)
                        send("OK")
                    }

                    command == "close" -> {
                        Sessions.removeSession(session.sessionId)
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
            val userData = call.getSession()
            val room = call.receiveText()
            try {
                userData.room = room
                Rooms.createRoom(room, listOf(userData))
                call.respond(HttpStatusCode.Created)
            } catch (e: Rooms.RoomExistsException) {
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/room") {
            val userData = call.getSession()
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
    val newOrder = Order(item, qty)
    if (qty == 0) {
        userData.orders.remove(newOrder)
    } else {
        userData.orders.add(newOrder)
    }
}

private fun ApplicationCall.getSession(): UserData {
    val session = sessions.get<UserSession>()
    if (Sessions.hasSession(session?.id)) {
        return Sessions.get(session!!.id)
    }
    val newSessionId = Sessions.newSession()
    sessions.set(UserSession(newSessionId))
    return Sessions.get(newSessionId)
}

private fun addToRoom(user: UserData, room: String) {
    user.room = room
    Rooms.get(room).users.add(user)
}