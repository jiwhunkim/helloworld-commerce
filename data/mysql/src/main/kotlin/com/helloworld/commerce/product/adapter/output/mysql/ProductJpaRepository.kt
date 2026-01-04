package com.helloworld.commerce.product.adapter.output.mysql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductJpaRepository : JpaRepository<ProductJpaEntity, Long> {
    fun findByNameContaining(name: String): List<ProductJpaEntity>
}
