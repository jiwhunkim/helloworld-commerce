package com.helloworld.commerce.product.adapter.output.mysql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InventoryJpaRepository : JpaRepository<InventoryJpaEntity, Long> {
    fun findByProductOptionId(productOptionId: Long): InventoryJpaEntity?
}
