package com.helloworld.commerce.product.domain

import org.jmolecules.ddd.annotation.AggregateRoot
import org.jmolecules.ddd.annotation.Identity
import java.time.LocalDateTime

@AggregateRoot
class Inventory(
    @Identity
    val id: Long? = null,
    productOptionId: Long,
    quantity: Int,
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now()
) {
    var productOptionId: Long = productOptionId
        private set
    var quantity: Int = quantity
        private set
    var createdAt: LocalDateTime = createdAt
        private set
    var updatedAt: LocalDateTime = updatedAt
        private set

    init {
        require(quantity >= 0) { "Inventory quantity cannot be negative" }
    }

    fun decreaseStock(amount: Int) {
        require(amount > 0) { "Decrease amount must be positive" }
        require(quantity >= amount) { "Insufficient stock. Available: $quantity, Requested: $amount" }
        this.quantity = quantity - amount
        this.updatedAt = LocalDateTime.now()
    }

    fun increaseStock(amount: Int) {
        require(amount > 0) { "Increase amount must be positive" }
        this.quantity = quantity + amount
        this.updatedAt = LocalDateTime.now()
    }

    fun isAvailable(): Boolean = quantity > 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Inventory) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Inventory(id=$id, productOptionId=$productOptionId, quantity=$quantity)"
    }
}
