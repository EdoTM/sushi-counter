package net.edotm

class Room(val name: String, val users: MutableSet<UserData> = mutableSetOf()) {
    fun aggregateAndGetOrders(): Map<String, Int> {
        val orders = users.flatMap { it.orders }
        return orders.groupingBy { it.name }.fold(0) { acc, order -> acc + order.quantity }
    }
}