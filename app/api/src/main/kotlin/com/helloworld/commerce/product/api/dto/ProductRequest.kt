package com.helloworld.commerce.product.api.dto

import java.math.BigDecimal

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: BigDecimal
)

data class UpdateProductInfoRequest(
    val name: String,
    val description: String
)

data class UpdateProductPriceRequest(
    val price: BigDecimal
)

data class CreateProductOptionRequest(
    val productId: Long,
    val name: String,
    val description: String,
    val initialStock: Int
)

data class UpdateProductOptionRequest(
    val name: String,
    val description: String
)

data class UpdateStockRequest(
    val amount: Int
)
