package com.helloworld.commerce.product.application.port.input

import com.helloworld.commerce.product.domain.ProductOption
import org.jmolecules.architecture.hexagonal.PrimaryPort

@PrimaryPort
interface CreateProductOptionUseCase {
    fun create(productId: Long, name: String, description: String, initialStock: Int): ProductOption
}
