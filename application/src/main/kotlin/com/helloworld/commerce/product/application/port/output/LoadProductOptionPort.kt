package com.helloworld.commerce.product.application.port.output

import com.helloworld.commerce.product.domain.Product
import com.helloworld.commerce.product.domain.ProductOption
import org.jmolecules.architecture.hexagonal.SecondaryPort

@SecondaryPort
interface LoadProductOptionPort {
    fun findById(id: Long): ProductOption?
    fun findByProductId(productId: Long): List<ProductOption>
}
