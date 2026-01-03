package com.helloworld.commerce.delivery.application

import com.helloworld.commerce.order.domain.OrderComplete
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class DeliveryService {
    val log = KotlinLogging.logger {}

    @ApplicationModuleListener
    fun on(orderComplete: OrderComplete) {
        log.info { "DeliveryService onMessage" }
    }
}
