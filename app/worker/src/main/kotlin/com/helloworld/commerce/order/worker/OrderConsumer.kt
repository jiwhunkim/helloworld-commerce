package com.helloworld.commerce.order.worker

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class OrderConsumer {
    val log = KotlinLogging.logger {}

    @KafkaListener(id = "orderEventListener", topics = ["order-event"], groupId = "order-worker-order-event")
    fun on() {
        log.info { "OrderConsumer onMessage" }
    }
}
