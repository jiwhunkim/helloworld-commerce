package com.helloworld.commerce.product.application.port.input

import com.helloworld.commerce.product.domain.Product
import org.jmolecules.architecture.hexagonal.PrimaryPort
import java.math.BigDecimal

@PrimaryPort
interface UpdateProductUseCase {
    fun updateInfo(productId: Long, name: String, description: String): Product
    fun updatePrice(productId: Long, price: BigDecimal): Product
}
