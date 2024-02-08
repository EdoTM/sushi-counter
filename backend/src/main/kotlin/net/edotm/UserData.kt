package net.edotm

class UserData(
    val sessionId: String, var room: String? = null, var expiration: Long = System.currentTimeMillis() + 3_600_000 * 3
) {
    val orders: List<Order>
        get() = _orders.toList()

    private val _orders = hashSetOf<Order>()

    fun clearRoomAndOrders() {
        room = null
        _orders.clear()
    }

    fun addOrder(order: Order) {
        _orders.removeIf { it.name == order.name }
        if (order.quantity > 0) {
            _orders.add(order)
        }
    }
}