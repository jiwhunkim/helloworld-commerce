package com.helloworld.commerce.domain

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Product domain model
 */
data class Product(
    val id: Long? = null,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val stockQuantity: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(price > BigDecimal.ZERO) { "Product price must be positive" }
        require(stockQuantity >= 0) { "Stock quantity cannot be negative" }
    }

    fun isInStock(): Boolean = stockQuantity > 0

    fun decreaseStock(quantity: Int): Product {
        require(quantity > 0) { "Decrease quantity must be positive" }
        require(stockQuantity >= quantity) { "Insufficient stock" }
        return copy(stockQuantity = stockQuantity - quantity, updatedAt = LocalDateTime.now())
    }

    fun increaseStock(quantity: Int): Product {
        require(quantity > 0) { "Increase quantity must be positive" }
        return copy(stockQuantity = stockQuantity + quantity, updatedAt = LocalDateTime.now())
    }
}
