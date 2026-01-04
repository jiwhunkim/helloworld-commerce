package com.helloworld.commerce.order.api

import com.helloworld.commerce.order.application.port.input.CompleteOrderUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(private val completeOrderUseCase: CompleteOrderUseCase) {

    @GetMapping("/orders/completed")
    fun complete() {
        completeOrderUseCase.complete(1L)
    }
}
