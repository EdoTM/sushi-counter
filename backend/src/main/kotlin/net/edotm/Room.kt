package net.edotm

import io.ktor.util.logging.*

class Room(val name: String, val users: MutableSet<UserData> = mutableSetOf()) {
    fun aggregateAndGetOrders(): Map<String, Int> {
        KtorSimpleLogger("Room").info("Aggregating orders for room $name with ${users.size} users")
        val orders = users.flatMap { it.orders }
        return orders.groupingBy { it.name }.fold(0) { acc, order -> acc + order.quantity }
    }
}