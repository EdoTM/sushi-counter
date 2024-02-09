package net.edotm

class Room(
    val name: String,
    val users: MutableSet<UserData> = mutableSetOf(),
    val expiration: Long = 3_600_000 * 3,
) {

    fun aggregateAndGetOrders(): Map<String, Int> {
        val orders = users.flatMap { it.orders }
        return orders.groupingBy { it.item }.fold(0) { acc, order -> acc + order.quantity }
    }
}