package com.helloworld.commerce.order.domain

import org.jmolecules.event.annotation.Externalized

@Externalized("order-event::#{#this.getId()}")
data class OrderComplete(val id: Long)
