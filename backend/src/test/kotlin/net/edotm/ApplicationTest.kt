package net.edotm

import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import net.edotm.plugins.configureRouting
import net.edotm.plugins.configureSessions
import net.edotm.plugins.configureWebSockets
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @BeforeTest
    fun setup() {
        Rooms.clear()
    }

    private fun ApplicationTestBuilder.setupTestApp() {
        application {
            configureWebSockets()
            configureRouting()
            configureSessions()
        }
    }

    private suspend fun ApplicationTestBuilder.setupTestRoom(client: HttpClient) {
        setupTestApp()
        client.put("/room") { setBody("Flying Elephant") }
    }

    @Test
    fun createRoom() = testApplication {
        setupTestApp()
        client.put("/room") {
            setBody("Flying Elephant")
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }
    }

    @Test
    fun createRoomTwice() = testApplication {
        setupTestApp()
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
        setupTestApp()
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
        setupTestApp()
        client.delete("/room").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }
}
