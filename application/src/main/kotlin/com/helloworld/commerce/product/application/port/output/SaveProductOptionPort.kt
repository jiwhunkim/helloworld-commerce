package com.helloworld.commerce.product.application.port.output

import com.helloworld.commerce.product.domain.ProductOption
import org.jmolecules.architecture.hexagonal.SecondaryPort

@SecondaryPort
interface SaveProductOptionPort {
    fun save(productOption: ProductOption): ProductOption
    fun delete(id: Long)
}
