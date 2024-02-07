package net.edotm

import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import net.edotm.plugins.configureRouting
import net.edotm.plugins.configureSessions
import net.edotm.plugins.configureWebSockets
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApplicationTest {
    @BeforeTest
    fun setup() {
        Rooms.clear()
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

    @Test(expected = ClosedReceiveChannelException::class)
    fun ifConnectToNonExistentRoom_ShouldClose() = testApplication {
        setupTestApp()
        val client = createClient {
            install(HttpCookies)
            install(WebSockets)
        }
        client.webSocket("/order") {
            incoming.receive() as Frame.Close
        }
    }

    @Test
    fun ifConnectToRoom_ThenReturnConnected() = testApplication {
        setupTestApp()
        val client = createClient {
            install(HttpCookies)
            install(WebSockets)
        }
        client.setupTestRoom()
        client.webSocket("/order") {
            val frame = incoming.receive() as Frame.Text
            assertEquals("Connected to Flying Elephant", frame.readText())
        }
    }
}

private fun ApplicationTestBuilder.setupTestApp() {
    application {
        configureWebSockets()
        configureRouting()
        configureSessions()
    }
}

private suspend fun HttpClient.setupTestRoom() {
    put("/room") { setBody("Flying Elephant") }
}