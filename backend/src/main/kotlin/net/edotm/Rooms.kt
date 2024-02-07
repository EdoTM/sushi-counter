package net.edotm

object Rooms {
    private val rooms: HashMap<String, Room> = hashMapOf()

    fun createRoom(code: String) {
        if (rooms.containsKey(code)) {
            throw RoomExistsException()
        }
        rooms[code] = Room(code)
    }

    fun remove(code: String) {
        rooms.remove(code) ?: throw RoomNotFoundException()
    }

    fun get(code: String): Room {
        return rooms[code] ?: throw RoomNotFoundException()
    }

    fun hasRoom(code: String): Boolean {
        return rooms.containsKey(code)
    }

    fun clear() {
        rooms.clear()
    }

    class RoomExistsException : Exception("Room already exists")
    class RoomNotFoundException : Exception("Room not found")
}