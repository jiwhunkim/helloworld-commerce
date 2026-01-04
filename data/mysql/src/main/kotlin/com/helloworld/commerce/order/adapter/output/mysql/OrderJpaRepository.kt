package com.helloworld.commerce.order.adapter.output.mysql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderJpaRepository : JpaRepository<OrderJpaEntity, Long> {

}
