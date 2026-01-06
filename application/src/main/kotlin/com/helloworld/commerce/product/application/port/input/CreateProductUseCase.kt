package com.helloworld.commerce.product.application.port.input

import com.helloworld.commerce.product.domain.Product
import org.jmolecules.architecture.hexagonal.PrimaryPort
import java.math.BigDecimal

@PrimaryPort
interface CreateProductUseCase {
    fun create(name: String, description: String, price: BigDecimal, stockQuantity: Int): Product
}
