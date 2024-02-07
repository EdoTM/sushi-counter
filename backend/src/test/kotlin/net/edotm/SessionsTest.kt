package net.edotm

import kotlin.test.Test

class SessionsTest {
    @Test
    fun addUserSession() {
        val sessionId = Sessions.newSession()
        println("sessionId: $sessionId")
    }
}