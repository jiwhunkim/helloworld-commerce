package com.helloworld.commerce.order.application.port.output

import org.jmolecules.architecture.hexagonal.SecondaryPort

@SecondaryPort
interface LoadOrderPort {
    fun findById(id: Long)
}
