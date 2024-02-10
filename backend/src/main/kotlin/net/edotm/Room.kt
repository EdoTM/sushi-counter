package net.edotm

import java.util.concurrent.ConcurrentHashMap

class Room(
    val name: String,
    val users: ConcurrentHashMap<UserData, HashSet<Order>> = ConcurrentHashMap(),
    val expiration: Long = 3_600_000 * 3,
) {
    fun getUserOrders(user: UserData): List<Order> {
        return users[user]?.toList() ?: throw UserNotFoundException()
    }

    fun getTotalOrders(): List<Order> {
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
        if (!users.containsKey(user)) throw UserNotFoundException()
        users[user]!!.remove(order)
        users[user]!!.add(order)
    }

    class UserNotFoundException : Exception("User not found")
}