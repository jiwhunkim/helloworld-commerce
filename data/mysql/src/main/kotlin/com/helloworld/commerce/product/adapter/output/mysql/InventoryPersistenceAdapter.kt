package com.helloworld.commerce.product.adapter.output.mysql

import com.helloworld.commerce.product.application.port.output.LoadInventoryPort
import com.helloworld.commerce.product.application.port.output.SaveInventoryPort
import com.helloworld.commerce.product.domain.Inventory
import org.jmolecules.architecture.hexagonal.SecondaryAdapter
import org.springframework.stereotype.Component
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@Component
@SecondaryAdapter
class InventoryPersistenceAdapter(
    private val inventoryJpaRepository: InventoryJpaRepository,
    private val productOptionJpaRepository: ProductOptionJpaRepository,
    @PersistenceContext
    private val entityManager: EntityManager
) : LoadInventoryPort, SaveInventoryPort {

    override fun findById(id: Long): Inventory? {
        val entity = inventoryJpaRepository.findById(id)
            .orElse(null) ?: return null
        
        return entity.toDomain()
    }

    override fun findByProductOptionId(productOptionId: Long): Inventory? {
        val entity = inventoryJpaRepository.findByProductOptionId(productOptionId)
            ?: return null
        
        return entity.toDomain()
    }

    override fun save(inventory: Inventory): Inventory {
        val productOption = productOptionJpaRepository.findById(inventory.productOptionId)
            .orElseThrow { IllegalArgumentException("Product option not found: ${inventory.productOptionId}") }
        
        val entity = if (inventory.id == null) {
            InventoryJpaEntity.from(inventory, productOption)
        } else {
            val existing = inventoryJpaRepository.findById(inventory.id!!).orElseThrow()
            existing.quantity = inventory.quantity
            existing.updatedAt = inventory.updatedAt
            existing
        }
        
        val saved = inventoryJpaRepository.save(entity)
        return saved.toDomain()
    }
}
