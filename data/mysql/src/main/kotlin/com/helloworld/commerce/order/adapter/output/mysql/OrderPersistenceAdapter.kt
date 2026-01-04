package com.helloworld.commerce.order.adapter.output.mysql

import com.helloworld.commerce.order.application.port.output.LoadOrderPort
import org.jmolecules.architecture.hexagonal.SecondaryAdapter
import org.springframework.stereotype.Service

@Service
@SecondaryAdapter
class OrderPersistenceAdapter(private val orderJpaRepository: OrderJpaRepository): LoadOrderPort {
    override fun findById(id: Long) {
        val source = orderJpaRepository.findById(id)
    }
}
