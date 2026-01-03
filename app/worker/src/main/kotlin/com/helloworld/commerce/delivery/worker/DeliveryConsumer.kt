package com.helloworld.commerce.delivery.worker

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class DeliveryConsumer {
    val log = KotlinLogging.logger {}

    @KafkaListener(id = "deliveryEventListener", topics = ["order-event"], groupId = "delivery-worker-order-event")
    fun on() {
        log.info { "DeliveryConsumer onMessage" }
    }
}
