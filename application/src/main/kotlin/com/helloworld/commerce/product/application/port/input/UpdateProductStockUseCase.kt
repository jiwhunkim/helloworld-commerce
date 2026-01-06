package com.helloworld.commerce.product.application.port.input

import org.jmolecules.architecture.hexagonal.PrimaryPort

@PrimaryPort
interface UpdateProductStockUseCase {
    fun increase(productOptionId: Long, amount: Int)
    fun decrease(productOptionId: Long, amount: Int)
}
