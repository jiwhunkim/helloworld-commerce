package com.helloworld.commerce.data.mysql

import com.helloworld.commerce.config.TestcontainersConfiguration
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
@SpringBootTest
class ApplicationIntegrationTests: DescribeSpec() {

    override val extensions: List<Extension> = listOf(SpringExtension())

    init {
        it("bootstrapsApplication") {

        }
    }
}
