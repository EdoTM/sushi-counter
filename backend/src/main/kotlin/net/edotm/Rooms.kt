package net.edotm

import io.ktor.util.logging.*
import java.util.concurrent.ConcurrentHashMap

const val MAX_ROOMS_PER_ADDRESS = 3

object Rooms {
    private val logger = KtorSimpleLogger("Rooms")

    private val rooms = ConcurrentHashMap<String, Room>()
    private val roomsPerAddress = ConcurrentHashMap<String, ArrayDeque<String>>()

    fun createRoom(name: String, creatorAddress: String): Room {
        if (rooms.containsKey(name)) {
            throw RoomExistsException()
        }
        if (!roomsPerAddress.containsKey(creatorAddress)) {
            roomsPerAddress[creatorAddress] = ArrayDeque()
        }
        val userRooms = roomsPerAddress[creatorAddress]!!
        if (userRooms.size >= MAX_ROOMS_PER_ADDRESS) {
            logger.warn("User from $creatorAddress tried to create more than $MAX_ROOMS_PER_ADDRESS rooms")
            remove(userRooms.removeFirst())
        }
        userRooms.addLast(name)
        val room = Room(name)
        rooms[name] = room
        return room
    }

    fun remove(name: String) {
        rooms.remove(name) ?: throw RoomNotFoundException()
    }

    fun get(name: String): Room {
        return rooms[name] ?: throw RoomNotFoundException()
    }

    fun clear() {
        rooms.clear()
    }

    class RoomExistsException : Exception("Room already exists")
    class RoomNotFoundException : Exception("Room not found")
}