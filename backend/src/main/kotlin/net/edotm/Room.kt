package net.edotm

import java.util.concurrent.ConcurrentHashMap

class Room(
    val name: String,
    val users: ConcurrentHashMap<UserData, HashSet<Order>> = ConcurrentHashMap(),
    val expiration: Long = 3_600_000 * 3,
) {

    fun aggregateAndGetOrders(): List<Order> {
        return users.values
            .flatten()
            .groupingBy { it.item }
            .fold(0) { acc, order -> acc + order.quantity }
            .map { Order(it.key, it.value) }
    }

    fun addUser(user: UserData) {
        users[user] = hashSetOf()
    }

    fun addUserOrder(user: UserData, order: Order) {
        users[user]?.add(order)
    }

    fun removeUser(user: UserData) {
        users.remove(user)
    }

    fun removeUserOrder(user: UserData, order: Order) {
        users[user]?.remove(order)
    }
}