package com.helloworld.commerce.order.application.port.input

import org.jmolecules.architecture.hexagonal.PrimaryPort

@PrimaryPort
interface CompleteOrderUseCase {
    fun complete(orderId: Long)
}
