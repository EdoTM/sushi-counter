package net.edotm

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import net.edotm.plugins.configureContentNegotiation
import net.edotm.plugins.configureRouting
import net.edotm.plugins.configureSessions
import net.edotm.plugins.configureWebSockets
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

const val testRoomName = "Flying Elephant"

class ApplicationTest {

    @BeforeTest
    fun setup() {
        Sessions.clear()
        Rooms.clear()
        Rooms.clearAddresses()
    }

    @Test
    fun createRoom() = testApplication {
        setupTestApp()
        client.put("/room") {
            setBody(testRoomName)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }
    }

    @Test
    fun createRoomTwice() = testApplication {
        setupTestApp()
        client.put("/room") {
            setBody(testRoomName)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }
        client.put("/room") {
            setBody(testRoomName)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun deleteExistingRoom() = testApplication {
        setupTestApp()
        val client = getHttpClient()
        client.put("/room") {
            setBody(testRoomName)
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
        val client = getHttpClient()
        client.webSocket("/order") {
            incoming.receive() as Frame.Close
        }
    }

    @Test
    fun ifConnectToRoom_ThenReturnConnected() = testApplication {
        setupTestApp()
        val client = getHttpClient()
        client.setupTestRoom()
        client.webSocket("/order") {
            val frame = incoming.receive() as Frame.Text
            assertEquals("Connected to $testRoomName", frame.readText())
        }
    }

    @Test
    fun addOrder() = testApplication {
        setupTestApp()
        val client = getHttpClient()
        client.setupTestRoom()
        client.webSocket("/order") {
            incoming.receive()
            send("order:Tea/2")
            assertEquals("OK", (incoming.receive() as Frame.Text).readText())
        }
        client.get("/orders").apply {
            println(body<String>())
            assertEquals(mapOf("Tea" to 2), body<Map<String, Int>>())
        }
    }

    @Test
    fun newOrderOverwritesOldOne() = testApplication {
        setupTestApp()
        val client = getHttpClient()
        client.setupTestRoom()
        client.webSocket("/order") {
            incoming.receive()
            send("order:Tea/2")
            assertEquals("OK", (incoming.receive() as Frame.Text).readText())
            send("order:Tea/3")
            assertEquals("OK", (incoming.receive() as Frame.Text).readText())
        }
        client.get("/orders").apply {
            assertEquals(mapOf("Tea" to 3), body<Map<String, Int>>())
        }
    }

    @Test
    fun ifCloseConnection_ShouldRemoveSession() = testApplication {
        setupTestApp()
        val client = getHttpClient()
        client.setupTestRoom()
        client.webSocket("/order") {
            incoming.receive()
            send("close")
        }
        client.get("/orders").apply {
            assertEquals(call.response.status, HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun ifRoomIsCreated_OtherUsersCanJoin() = testApplication {
        setupTestApp()
        val client1 = getHttpClient()
        val client2 = getHttpClient()
        client1.setupTestRoom()
        client2.post("/room/join") { setBody(testRoomName) }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun joinedUsersCanPlaceOrders() = testApplication {
        setupTestApp()
        val client1 = getHttpClient()
        val client2 = getHttpClient()
        client1.setupTestRoom()
        client2.post("/room/join") { setBody(testRoomName) }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        client2.webSocket("/order") {
            incoming.receive()
            send("order:Coffee/1")
            assertEquals("OK", (incoming.receive() as Frame.Text).readText())
        }
        client2.get("/orders").apply {
            assertEquals(mapOf("Coffee" to 1), body<Map<String, Int>>())
        }
    }

    @Test
    fun getTotalOrders() = testApplication {
        setupTestApp()
        val client1 = getHttpClient()
        val client2 = getHttpClient()
        client1.setupTestRoom()
        client2.post("/room/join") { setBody(testRoomName) }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        client1.webSocket("/order") {
            incoming.receive()
            send("order:Tea/2")
            assertEquals("OK", (incoming.receive() as Frame.Text).readText())
        }
        client2.webSocket("/order") {
            incoming.receive()
            send("order:Coffee/1")
            assertEquals("OK", (incoming.receive() as Frame.Text).readText())
        }
        client2.get("/room/total").apply {
            assertEquals(mapOf("Tea" to 2, "Coffee" to 1), body<Map<String, Int>>())
        }
    }

    @Test
    fun getTotalOrdersGiveAggregatedOrders() = testApplication {
        setupTestApp()
        val client1 = getHttpClient()
        val client2 = getHttpClient()
        client1.setupTestRoom()
        client2.post("/room/join") { setBody(testRoomName) }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        client1.webSocket("/order") {
            incoming.receive()
            send("order:Coffee/2")
            assertEquals("OK", (incoming.receive() as Frame.Text).readText())
        }
        client2.webSocket("/order") {
            incoming.receive()
            send("order:Coffee/1")
            assertEquals("OK", (incoming.receive() as Frame.Text).readText())
        }
        client2.get("/room/total").apply {
            assertEquals(mapOf("Coffee" to 3), body<Map<String, Int>>())
        }
    }
}

private fun ApplicationTestBuilder.getHttpClient(): HttpClient {
    return createClient {
        install(HttpCookies)
        install(WebSockets)
        install(ContentNegotiation) { json() }
    }
}

private fun ApplicationTestBuilder.setupTestApp() {
    application {
        configureContentNegotiation()
        configureWebSockets()
        configureRouting()
        configureSessions("123")
    }
}

private suspend fun HttpClient.setupTestRoom() {
    put("/room") { setBody(testRoomName) }
}