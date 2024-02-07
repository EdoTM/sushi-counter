package net.edotm

class Room(val code: String) {
    private val users = mutableSetOf<UserSession>()
}