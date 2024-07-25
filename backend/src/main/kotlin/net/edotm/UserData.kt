package net.edotm

class UserData(
    val sessionId: String,
    var currentRoom: String? = null,
    var expiration: Long = System.currentTimeMillis() + 3_600_000 * 3
)