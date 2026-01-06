package com.helloworld.commerce.product.application.port.input

import com.helloworld.commerce.product.domain.Product
import org.jmolecules.architecture.hexagonal.PrimaryPort

@PrimaryPort
interface GetProductUseCase {
    fun getById(id: Long): Product?
    fun getAll(): List<Product>
    fun searchByName(name: String): List<Product>
}
