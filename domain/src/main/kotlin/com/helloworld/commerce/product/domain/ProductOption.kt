package com.helloworld.commerce.product.domain

import org.jmolecules.ddd.annotation.AggregateRoot
import org.jmolecules.ddd.annotation.Identity
import java.time.LocalDateTime

@AggregateRoot
class ProductOption(
    @Identity
    val id: Long? = null,
    productId: Long,
    name: String,
    description: String,
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now()
) {
    var productId: Long = productId
        private set
    var name: String = name
        private set
    var description: String = description
        private set
    var createdAt: LocalDateTime = createdAt
        private set
    var updatedAt: LocalDateTime = updatedAt
        private set

    init {
        require(name.isNotBlank()) { "Product option name cannot be blank" }
    }

    fun updateInfo(newName: String, newDescription: String) {
        require(newName.isNotBlank()) { "Product option name cannot be blank" }
        this.name = newName
        this.description = newDescription
        this.updatedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductOption) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "ProductOption(id=$id, name=$name, productId=$productId)"
    }
}
