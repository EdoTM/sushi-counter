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
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import net.edotm.*
import net.edotm.Sessions
import java.nio.charset.Charset

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
            val roomName = userData.currentRoom
            if (roomName == null || !Rooms.hasRoom(roomName)) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No room"))
                return@webSocket
            }
            send("Connected to $roomName")
            val room = Rooms.get(roomName)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val order = deserialize<Order>(frame)
                try {
                    room.addUserOrder(userData, order)
                    send("OK")
                } catch (e: Room.UserNotFoundException) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User not found"))
                    return@webSocket
                }
            }
        }

        get("/orders") {
            val userData = call.getSession()
            val room = userData.currentRoom
            if (room == null || !Rooms.hasRoom(room)) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            val orders = Rooms
                .get(room)
                .getUserOrders(userData)
                .associate { it.item to it.quantity }
            call.respond(orders)
        }

        put("/room") {
            val room = retrieveRoomFromRequest()
            val userData = call.getOrCreateSession()
            try {
                Rooms.createRoom(room, call.request.local.remoteAddress)
                Rooms.get(room).addUser(userData)
                userData.currentRoom = room
                logger.info("User from ${call.request.local.remoteAddress} created room $room")
                call.respond(HttpStatusCode.Created)
            } catch (e: Rooms.RoomExistsException) {
                call.respond(HttpStatusCode.OK)
            }
        }

        post("/room/join") {
            val room = retrieveRoomFromRequest()
            val userData = call.getOrCreateSession()
            if (userData.currentRoom == room) {
                call.respond(HttpStatusCode.OK)
                return@post
            }
            try {
                Rooms.get(room).addUser(userData)
                userData.currentRoom = room
                logger.info("User joined room $room")
                call.respond(HttpStatusCode.OK)
            } catch (e: Rooms.RoomNotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/room/total") {
            val userData = call.getSession()
            val room = userData.currentRoom
            if (room == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            try {
                val orders = Rooms.get(room).getTotalOrders()
                call.respond(orders)
            } catch (e: Rooms.RoomNotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.retrieveRoomFromRequest(): String {
    try {
        return normalize(call.receiveText())
    } catch (e: Exception) {
        throw BadRequestException("Invalid room name")
    }
}

private suspend inline fun <reified T> DefaultWebSocketServerSession.deserialize(frame: Frame): T {
    return converter!!.deserialize(
        Charset.defaultCharset(),
        TypeInfo(T::class, T::class.java),
        content = frame
    ) as T
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

private fun normalize(s: String): String {
    return s.trim().replace(" ", "").lowercase()
}