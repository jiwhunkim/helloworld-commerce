package com.helloworld.commerce.product.application.port.output

import com.helloworld.commerce.product.domain.Product
import org.jmolecules.architecture.hexagonal.SecondaryPort

@SecondaryPort
interface LoadProductPort {
    fun findById(id: Long): Product?
    fun findAll(): List<Product>
    fun findByNameContaining(name: String): List<Product>
}
