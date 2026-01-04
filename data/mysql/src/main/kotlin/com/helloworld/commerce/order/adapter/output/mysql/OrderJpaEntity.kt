package com.helloworld.commerce.order.adapter.output.mysql

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class OrderJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false, precision = 19, scale = 2)
    var price: BigDecimal,

    @Column(nullable = false)
    var stockQuantity: Int,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

}
