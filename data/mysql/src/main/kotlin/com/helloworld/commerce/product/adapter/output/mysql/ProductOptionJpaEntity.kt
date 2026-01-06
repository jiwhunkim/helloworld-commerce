package com.helloworld.commerce.product.adapter.output.mysql

import com.helloworld.commerce.product.domain.ProductOption
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "product_options")
class ProductOptionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductJpaEntity,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @OneToOne(mappedBy = "productOption", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val inventory: InventoryJpaEntity? = null

    fun toDomain(): ProductOption = ProductOption(
        id = id,
        productId = product.id!!,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun from(productOption: ProductOption, product: ProductJpaEntity): ProductOptionJpaEntity = ProductOptionJpaEntity(
            id = productOption.id,
            product = product,
            name = productOption.name,
            description = productOption.description,
            createdAt = productOption.createdAt,
            updatedAt = productOption.updatedAt
        )
    }
}
