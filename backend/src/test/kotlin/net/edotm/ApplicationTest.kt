package net.edotm

import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import net.edotm.plugins.configureRouting
import net.edotm.plugins.configureSessions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @BeforeTest
    fun resetRooms() {
        Rooms.clear()
    }

    @Test
    fun createRoom() = testApplication {
        application {
            configureRouting()
            configureSessions()
        }
        client.put("/room") {
            setBody("Flying Elephant")
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }
    }

    @Test
    fun createRoomTwice() = testApplication {
        application {
            configureRouting()
            configureSessions()
        }
        client.put("/room") {
            setBody("Flying Elephant")
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }
        client.put("/room") {
            setBody("Flying Elephant")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun deleteExistingRoom() = testApplication {
        application {
            configureRouting()
            configureSessions()
        }
        val client = createClient {
            install(HttpCookies)
        }
        client.put("/room") {
            setBody("Flying Elephant")
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }
        client.delete("/room").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun ifDeleteNonExistentRoom_ThenReturnNotFound() = testApplication {
        application {
            configureRouting()
            configureSessions()
        }
        client.delete("/room").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }
}
