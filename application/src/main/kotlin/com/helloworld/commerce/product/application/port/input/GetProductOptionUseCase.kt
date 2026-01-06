package com.helloworld.commerce.product.application.port.input

import com.helloworld.commerce.product.domain.ProductOption
import org.jmolecules.architecture.hexagonal.PrimaryPort

@PrimaryPort
interface GetProductOptionUseCase {
    fun findById(id: Long): ProductOption?
    fun findByProductId(productId: Long): List<ProductOption>
}
