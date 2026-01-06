package com.helloworld.commerce.product.domain

import org.jmolecules.ddd.annotation.AggregateRoot
import org.jmolecules.ddd.annotation.Identity
import java.math.BigDecimal
import java.time.LocalDateTime

@AggregateRoot
class Product(
    @Identity
    val id: Long? = null,
    name: String,
    description: String,
    price: BigDecimal,
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now()
) {
    var name: String = name
        private set
    var description: String = description
        private set
    var price: BigDecimal = price
        private set
    var createdAt: LocalDateTime = createdAt
        private set
    var updatedAt: LocalDateTime = updatedAt
        private set

    init {
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(price > BigDecimal.ZERO) { "Product price must be positive" }
    }

    fun updatePrice(newPrice: BigDecimal) {
        require(newPrice > BigDecimal.ZERO) { "Product price must be positive" }
        this.price = newPrice
        this.updatedAt = LocalDateTime.now()
    }

    fun updateInfo(newName: String, newDescription: String) {
        require(newName.isNotBlank()) { "Product name cannot be blank" }
        this.name = newName
        this.description = newDescription
        this.updatedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Product) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Product(id=$id, name=$name, price=$price)"
    }
}
