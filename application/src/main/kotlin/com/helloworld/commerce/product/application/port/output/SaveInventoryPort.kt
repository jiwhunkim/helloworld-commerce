package com.helloworld.commerce.product.application.port.output

import com.helloworld.commerce.product.domain.Inventory
import org.jmolecules.architecture.hexagonal.SecondaryPort

@SecondaryPort
interface SaveInventoryPort {
    fun save(inventory: Inventory): Inventory
}
