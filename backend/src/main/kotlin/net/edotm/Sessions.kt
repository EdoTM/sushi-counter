package net.edotm

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.NoSuchElementException

object Sessions {
    private val sessions: ConcurrentHashMap<String, UserData> = ConcurrentHashMap()

    fun newSession(): String {
        val sessionId = UUID.randomUUID().toString()
        sessions[sessionId] = UserData(sessionId)
        return sessionId
    }

    fun removeSession(sessionId: String) {
        sessions.remove(sessionId) ?: throw SessionNotFoundException()
    }

    fun get(sessionId: String): UserData {
        val session = sessions[sessionId] ?: throw SessionNotFoundException()
        if (session.sessionExpiration < System.currentTimeMillis()) {
            removeSession(sessionId)
            throw SessionNotFoundException()
        }
        return session
    }

    fun hasSession(sessionId: String?): Boolean {
        return sessions.containsKey(sessionId)
    }

    fun invalidateExpiredSessions() {
        val now = System.currentTimeMillis()
        sessions.entries.removeIf { it.value.sessionExpiration < now }
    }

    class SessionNotFoundException : NoSuchElementException("Session not found")
}