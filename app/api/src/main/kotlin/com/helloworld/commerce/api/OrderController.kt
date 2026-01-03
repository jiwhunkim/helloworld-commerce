package com.helloworld.commerce.api

import com.helloworld.commerce.order.application.OrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(private val orderService: OrderService) {

    @GetMapping("/orders/completed")
    fun complete() {
        orderService.complete()
    }
}
