package com.helloworld.commerce.product.application.port.output

import com.helloworld.commerce.product.domain.Inventory
import org.jmolecules.architecture.hexagonal.SecondaryPort

@SecondaryPort
interface LoadInventoryPort {
    fun findById(id: Long): Inventory?
    fun findByProductOptionId(productOptionId: Long): Inventory?
}
