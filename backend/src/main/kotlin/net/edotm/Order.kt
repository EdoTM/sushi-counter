package net.edotm

data class Order(
    val name: String,
    val quantity: Int,
) {
    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        return name == other.name
    }
}