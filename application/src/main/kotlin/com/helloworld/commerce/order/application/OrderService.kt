package com.helloworld.commerce.order.application

import com.helloworld.commerce.order.domain.OrderComplete
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(val events: ApplicationEventPublisher) {
    @Transactional
    fun complete() {
        events.publishEvent(OrderComplete(1L))
    }

    val log = KotlinLogging.logger {}

    @ApplicationModuleListener
    fun on(orderComplete: OrderComplete) {
        log.info { "OrderService onMessage" }
    }
}
