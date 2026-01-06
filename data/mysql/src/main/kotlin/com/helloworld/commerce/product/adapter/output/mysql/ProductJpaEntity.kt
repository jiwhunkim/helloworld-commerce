package com.helloworld.commerce.product.adapter.output.mysql

import com.helloworld.commerce.product.domain.Product
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class ProductJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false, precision = 19, scale = 2)
    var price: BigDecimal,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val options: List<ProductOptionJpaEntity> = emptyList()

    fun toDomain(): Product = Product(
        id = id,
        name = name,
        description = description,
        price = price,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun from(product: Product): ProductJpaEntity = ProductJpaEntity(
            id = product.id,
            name = product.name,
            description = product.description,
            price = product.price,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt
        )
    }
}
