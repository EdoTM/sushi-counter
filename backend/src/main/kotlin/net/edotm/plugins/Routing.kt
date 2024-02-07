package net.edotm.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.edotm.Rooms
import net.edotm.UserSession

fun Application.configureRouting() {
    fun getSession(call: ApplicationCall): UserSession {
        val session = call.sessions.get<UserSession>()
        if (session != null) {
            return session
        }
        val newSession = UserSession()
        call.sessions.set(newSession)
        return newSession
    }

    routing {
        put("/room") {
            val session = getSession(call)
            val room = call.receiveText()
            try {
                Rooms.createRoom(room)
                call.sessions.set(session.copy(room = room))
                call.respond(HttpStatusCode.Created)
            } catch (e: Rooms.RoomExistsException) {
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/room") {
            val session = getSession(call)
            if (session.room == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            try {
                Rooms.remove(session.room)
                call.sessions.set(session.copy(room = null))
                call.respond(HttpStatusCode.OK)
            } catch (e: Rooms.RoomNotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
