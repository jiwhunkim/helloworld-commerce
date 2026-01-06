package com.helloworld.commerce.product.adapter.output.mysql

import com.helloworld.commerce.product.domain.Inventory
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "inventories")
class InventoryJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", nullable = false, unique = true)
    val productOption: ProductOptionJpaEntity,

    @Column(nullable = false)
    var quantity: Int,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Inventory = Inventory(
        id = id,
        productOptionId = productOption.id!!,
        quantity = quantity,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun from(inventory: Inventory, productOption: ProductOptionJpaEntity): InventoryJpaEntity = InventoryJpaEntity(
            id = inventory.id,
            productOption = productOption,
            quantity = inventory.quantity,
            createdAt = inventory.createdAt,
            updatedAt = inventory.updatedAt
        )
    }
}
