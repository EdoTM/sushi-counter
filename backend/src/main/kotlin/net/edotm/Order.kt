package net.edotm

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val item: String,
    val quantity: Int,
) {
    override fun hashCode(): Int {
        return item.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        return item == other.item
    }
}