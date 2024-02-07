package net.edotm

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SessionsTest {
    @Test
    fun addUserSession() {
        val sessionId = Sessions.newSession()
        assertTrue { sessionId.length == 36 }
        assertTrue { Sessions.hasSession(sessionId) }
    }

    @Test(expected = Sessions.SessionNotFoundException::class)
    fun ifSessionExpires_thenItIsRemoved() {
        val sessionId = Sessions.newSession()
        val session = Sessions.get(sessionId)
        session.expiration = System.currentTimeMillis() - 1
        assertFalse { Sessions.hasSession(sessionId) }
    }

    @Test(expected = Sessions.SessionNotFoundException::class)
    fun ifGetNonExistentSession_thenThrowException() {
        Sessions.get("non-existent-session")
    }

    @Test(expected = Sessions.SessionNotFoundException::class)
    fun ifRemoveNonExistentSession_thenThrowException() {
        Sessions.removeSession("non-existent-session")
    }
}
