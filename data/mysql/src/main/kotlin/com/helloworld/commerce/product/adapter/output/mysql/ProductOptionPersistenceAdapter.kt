package com.helloworld.commerce.product.adapter.output.mysql

import com.helloworld.commerce.product.application.port.output.LoadProductOptionPort
import com.helloworld.commerce.product.application.port.output.SaveProductOptionPort
import com.helloworld.commerce.product.domain.ProductOption
import org.jmolecules.architecture.hexagonal.SecondaryAdapter
import org.springframework.stereotype.Component
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@Component
@SecondaryAdapter
class ProductOptionPersistenceAdapter(
    private val productOptionJpaRepository: ProductOptionJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    @PersistenceContext
    private val entityManager: EntityManager
) : LoadProductOptionPort, SaveProductOptionPort {

    override fun findById(id: Long): ProductOption? {
        val entity = productOptionJpaRepository.findById(id)
            .orElse(null) ?: return null
        
        return entity.toDomain()
    }

    override fun findByProductId(productId: Long): List<ProductOption> {
        return productOptionJpaRepository.findByProductId(productId)
            .map { it.toDomain() }
    }

    override fun save(productOption: ProductOption): ProductOption {
        val product = productJpaRepository.findById(productOption.productId)
            .orElseThrow { IllegalArgumentException("Product not found: ${productOption.productId}") }
        
        val entity = if (productOption.id == null) {
            ProductOptionJpaEntity.from(productOption, product)
        } else {
            val existing = productOptionJpaRepository.findById(productOption.id!!).orElseThrow()
            existing.name = productOption.name
            existing.description = productOption.description
            existing.updatedAt = productOption.updatedAt
            existing
        }
        
        val saved = productOptionJpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(id: Long) {
        productOptionJpaRepository.deleteById(id)
    }
}
