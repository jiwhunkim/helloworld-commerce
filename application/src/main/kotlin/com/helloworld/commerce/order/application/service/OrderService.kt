package com.helloworld.commerce.order.application.service

import com.helloworld.commerce.order.application.port.input.CompleteOrderUseCase
import com.helloworld.commerce.order.domain.OrderComplete
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(val events: ApplicationEventPublisher): CompleteOrderUseCase {
    @Transactional
    override fun complete(orderId: Long) {
        events.publishEvent(OrderComplete(1L))
    }

    val log = KotlinLogging.logger {}

    @ApplicationModuleListener
    fun on(orderComplete: OrderComplete) {
        log.info { "OrderService onMessage" }
    }
}
