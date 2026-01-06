package com.helloworld.commerce.product.application.port.input

import com.helloworld.commerce.product.domain.ProductOption
import org.jmolecules.architecture.hexagonal.PrimaryPort

@PrimaryPort
interface UpdateProductOptionUseCase {
    fun updateInfo(id: Long, name: String, description: String): ProductOption
}
