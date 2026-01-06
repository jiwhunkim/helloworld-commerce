package com.helloworld.commerce.product.adapter.output.mysql

import com.helloworld.commerce.product.application.port.output.LoadProductPort
import com.helloworld.commerce.product.application.port.output.SaveProductPort
import com.helloworld.commerce.product.domain.Product
import org.jmolecules.architecture.hexagonal.SecondaryAdapter
import org.springframework.stereotype.Component
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@Component
@SecondaryAdapter
class ProductPersistenceAdapter(
    private val productJpaRepository: ProductJpaRepository,
    @PersistenceContext
    private val entityManager: EntityManager
) : LoadProductPort, SaveProductPort {

    override fun findById(id: Long): Product? {
        val entity = productJpaRepository.findById(id)
            .orElse(null) ?: return null
        
        return entity.toDomain()
    }

    override fun findAll(): List<Product> {
        return productJpaRepository.findAll()
            .map { it.toDomain() }
    }

    override fun findByNameContaining(name: String): List<Product> {
        return productJpaRepository.findByNameContaining(name)
            .map { it.toDomain() }
    }

    override fun save(product: Product): Product {
        val entity = if (product.id == null) {
            ProductJpaEntity.from(product)
        } else {
            val existing = productJpaRepository.findById(product.id!!).orElseThrow()
            existing.name = product.name
            existing.description = product.description
            existing.price = product.price
            existing.updatedAt = product.updatedAt
            existing
        }
        
        val saved = productJpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(id: Long) {
        productJpaRepository.deleteById(id)
    }
}
