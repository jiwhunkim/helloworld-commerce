package com.helloworld.commerce.product.application.port.output

import com.helloworld.commerce.product.domain.Product
import org.jmolecules.architecture.hexagonal.SecondaryPort

@SecondaryPort
interface SaveProductPort {
    fun save(product: Product): Product
    fun delete(id: Long)
}
