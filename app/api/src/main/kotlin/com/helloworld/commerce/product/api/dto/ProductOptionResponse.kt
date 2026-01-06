package com.helloworld.commerce.product.api.dto

import com.helloworld.commerce.product.domain.ProductOption
import java.time.LocalDateTime

data class ProductOptionResponse(
    val id: Long,
    val productId: Long,
    val name: String,
    val description: String,
    val stockQuantity: Int,
    val isAvailable: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(productOption: ProductOption, stockQuantity: Int): ProductOptionResponse = ProductOptionResponse(
            id = productOption.id!!,
            productId = productOption.productId,
            name = productOption.name,
            description = productOption.description,
            stockQuantity = stockQuantity,
            isAvailable = stockQuantity > 0,
            createdAt = productOption.createdAt,
            updatedAt = productOption.updatedAt
        )
    }
}
