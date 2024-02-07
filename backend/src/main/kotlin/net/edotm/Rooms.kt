package net.edotm

import java.util.concurrent.ConcurrentHashMap

object Rooms {
    private val rooms: ConcurrentHashMap<String, Room> = ConcurrentHashMap()

    fun createRoom(name: String): Room {
        if (rooms.containsKey(name)) {
            throw RoomExistsException()
        }
        val room = Room(name)
        rooms[name] = room
        return room
    }

    fun createRoom(name: String, sessions: Iterable<UserData>) {
        createRoom(name)
        rooms[name]!!.users.addAll(sessions)
    }

    fun remove(name: String) {
        rooms.remove(name) ?: throw RoomNotFoundException()
    }

    fun get(name: String): Room {
        return rooms[name] ?: throw RoomNotFoundException()
    }

    fun hasRoom(name: String): Boolean {
        return rooms.containsKey(name)
    }

    fun clear() {
        rooms.clear()
    }

    class RoomExistsException : Exception("Room already exists")
    class RoomNotFoundException : Exception("Room not found")
}