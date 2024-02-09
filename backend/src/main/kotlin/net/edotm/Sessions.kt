package net.edotm

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.NoSuchElementException

object Sessions {
    private val sessions: ConcurrentHashMap<String, UserData> = ConcurrentHashMap()
    var sessionExpirationMillis: Long = 3_600_000 * 3

    fun newSession(): String {
        val sessionId = UUID.randomUUID().toString()
        val millisNow = System.currentTimeMillis()
        sessions[sessionId] = UserData(sessionId, expiration = millisNow + sessionExpirationMillis)
        return sessionId
    }

    fun removeSession(sessionId: String) {
        sessions.remove(sessionId) ?: throw SessionNotFoundException()
    }

    fun get(sessionId: String): UserData {
        val session = sessions[sessionId] ?: throw SessionNotFoundException()
        if (session.expiration < System.currentTimeMillis()) {
            removeSession(sessionId)
            throw SessionNotFoundException()
        }
        return session
    }

    fun hasSession(sessionId: String?): Boolean {
        if (sessionId == null) return false
        val session = sessions[sessionId] ?: return false
        if (session.expiration < System.currentTimeMillis()) {
            removeSession(sessionId)
            return false
        }
        return true
    }

    fun invalidateExpiredSessions() {
        val now = System.currentTimeMillis()
        sessions.entries.removeIf { it.value.expiration < now }
    }

    class SessionNotFoundException : NoSuchElementException("Session not found")
}