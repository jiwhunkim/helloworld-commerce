package com.helloworld.commerce.product.application.service

import com.helloworld.commerce.product.application.port.input.UpdateProductStockUseCase
import com.helloworld.commerce.product.application.port.output.LoadInventoryPort
import com.helloworld.commerce.product.application.port.output.SaveInventoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class InventoryService(
    private val loadInventoryPort: LoadInventoryPort,
    private val saveInventoryPort: SaveInventoryPort
) : UpdateProductStockUseCase {

    @Transactional
    override fun increase(productOptionId: Long, amount: Int) {
        log.info { "Increasing stock: productOptionId=$productOptionId, amount=$amount" }

        val inventory = loadInventoryPort.findByProductOptionId(productOptionId)
            ?: throw IllegalArgumentException("Inventory not found for product option: $productOptionId")

        inventory.increaseStock(amount)
        saveInventoryPort.save(inventory)

        log.info { "Stock increased: productOptionId=$productOptionId, newStock=${inventory.quantity}" }
    }

    @Transactional
    override fun decrease(productOptionId: Long, amount: Int) {
        log.info { "Decreasing stock: productOptionId=$productOptionId, amount=$amount" }

        val inventory = loadInventoryPort.findByProductOptionId(productOptionId)
            ?: throw IllegalArgumentException("Inventory not found for product option: $productOptionId")

        inventory.decreaseStock(amount)
        saveInventoryPort.save(inventory)

        log.info { "Stock decreased: productOptionId=$productOptionId, remaining=${inventory.quantity}" }
    }
}
