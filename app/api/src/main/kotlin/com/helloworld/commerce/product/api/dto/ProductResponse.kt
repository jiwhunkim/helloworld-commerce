package com.helloworld.commerce.product.api.dto

import com.helloworld.commerce.product.domain.Product
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(product: Product): ProductResponse = ProductResponse(
            id = product.id!!,
            name = product.name,
            description = product.description,
            price = product.price,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt
        )
    }
}
