package com.helloworld.commerce.order

import com.helloworld.commerce.domain.Product
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class OrderJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false, precision = 19, scale = 2)
    var price: BigDecimal,

    @Column(nullable = false)
    var stockQuantity: Int,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            stockQuantity = stockQuantity,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun from(product: Product): OrderJpaEntity {
            return OrderJpaEntity(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                stockQuantity = product.stockQuantity,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }
}
