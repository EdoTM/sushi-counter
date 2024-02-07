package net.edotm

class UserData(
    val sessionId: String,
    var room: String? = null,
    val orders: MutableSet<Order> = mutableSetOf(),
    var expiration: Long = System.currentTimeMillis() + 3600 * 3
) {
    fun leaveRoom() {
        room = null
        orders.clear()
    }
}