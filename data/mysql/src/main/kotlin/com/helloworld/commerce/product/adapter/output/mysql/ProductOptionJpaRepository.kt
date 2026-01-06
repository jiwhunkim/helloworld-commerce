package com.helloworld.commerce.product.adapter.output.mysql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOptionJpaRepository : JpaRepository<ProductOptionJpaEntity, Long> {
    fun findByProductId(productId: Long): List<ProductOptionJpaEntity>
    fun findByProductIdAndNameContaining(productId: Long, name: String): List<ProductOptionJpaEntity>
}
