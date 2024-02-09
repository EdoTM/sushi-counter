package net.edotm

import io.ktor.util.logging.*
import java.util.concurrent.ConcurrentHashMap


object Rooms {
    var maxRoomsPerAddress = 3
    var roomExpirationMillis: Long = 3_600_000 * 3

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
        if (userRooms.size >= maxRoomsPerAddress) {
            logger.warn("User from $creatorAddress tried to create more than $maxRoomsPerAddress rooms")
            remove(userRooms.removeFirst())
        }
        userRooms.addLast(name)
        val millisNow = System.currentTimeMillis()
        val room = Room(name, expiration = millisNow + roomExpirationMillis)
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

    fun clearAddresses() {
        roomsPerAddress.clear()
    }

    fun cleanupEmptyRooms() {
        val millisNow = System.currentTimeMillis()
        rooms.entries.removeIf {
            it.value.users.isEmpty() && it.value.expiration < millisNow
        }
    }

    class RoomExistsException : Exception("Room already exists")
    class RoomNotFoundException : Exception("Room not found")
}