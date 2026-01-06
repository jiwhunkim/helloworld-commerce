package com.helloworld.commerce.product.application.service

import com.helloworld.commerce.product.application.port.output.LoadInventoryPort
import com.helloworld.commerce.product.application.port.output.LoadProductOptionPort
import com.helloworld.commerce.product.application.port.output.LoadProductPort
import com.helloworld.commerce.product.application.port.output.SaveInventoryPort
import com.helloworld.commerce.product.application.port.output.SaveProductOptionPort
import com.helloworld.commerce.product.domain.Inventory
import com.helloworld.commerce.product.domain.Product
import com.helloworld.commerce.product.domain.ProductOption
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ProductOptionServiceSpec : DescribeSpec({
    val loadProductOptionPort = mockk<LoadProductOptionPort>()
    val saveProductOptionPort = mockk<SaveProductOptionPort>()
    val loadProductPort = mockk<LoadProductPort>()
    val loadInventoryPort = mockk<LoadInventoryPort>()
    val saveInventoryPort = mockk<SaveInventoryPort>()
    val productOptionService = ProductOptionService(
        loadProductOptionPort,
        saveProductOptionPort,
        loadProductPort,
        loadInventoryPort,
        saveInventoryPort
    )

    describe("create") {
        it("should create product option with initial stock") {
            val product = Product(
                id = 1,
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = java.math.BigDecimal("1299000")
            )
            val productOption = ProductOption(
                id = 1,
                productId = 1,
                name = "Black 256GB",
                description = "Black color with 256GB storage"
            )
            val inventory = Inventory(
                id = 1,
                productOptionId = 1,
                quantity = 100
            )

            every { loadProductPort.findById(1) } returns product
            every { saveProductOptionPort.save(any()) } returns productOption
            every { saveInventoryPort.save(any()) } returns inventory

            val result = productOptionService.create(
                productId = 1,
                name = "Black 256GB",
                description = "Black color with 256GB storage",
                initialStock = 100
            )

            result.id shouldBe 1
            result.productId shouldBe 1
            result.name shouldBe "Black 256GB"
            verify { saveProductOptionPort.save(any()) }
            verify { saveInventoryPort.save(any()) }
        }

        it("should throw exception when product not found") {
            every { loadProductPort.findById(1) } returns null

            val exception = shouldThrow<IllegalArgumentException> {
                productOptionService.create(
                    productId = 1,
                    name = "Black 256GB",
                    description = "Black color with 256GB storage",
                    initialStock = 100
                )
            }

            exception.message shouldBe "Product not found: 1"
        }
    }

    describe("findById") {
        it("should return product option when exists") {
            val productOption = ProductOption(
                id = 1,
                productId = 1,
                name = "Black 256GB",
                description = "Black color with 256GB storage"
            )

            every { loadProductOptionPort.findById(1) } returns productOption

            val result = productOptionService.findById(1)

            result?.id shouldBe 1
            result?.name shouldBe "Black 256GB"
        }

        it("should return null when product option does not exist") {
            every { loadProductOptionPort.findById(1) } returns null

            val result = productOptionService.findById(1)

            result shouldBe null
        }
    }

    describe("findByProductId") {
        it("should return product options for given product") {
            val productOptions = listOf(
                ProductOption(
                    id = 1,
                    productId = 1,
                    name = "Black 256GB",
                    description = "Black color with 256GB storage"
                ),
                ProductOption(
                    id = 2,
                    productId = 1,
                    name = "White 256GB",
                    description = "White color with 256GB storage"
                )
            )

            every { loadProductOptionPort.findByProductId(1) } returns productOptions

            val result = productOptionService.findByProductId(1)

            result.size shouldBe 2
            result[0].name shouldBe "Black 256GB"
            result[1].name shouldBe "White 256GB"
        }
    }

    describe("updateInfo") {
        it("should update product option info successfully") {
            val productOption = ProductOption(
                id = 1,
                productId = 1,
                name = "Black 256GB",
                description = "Black color with 256GB storage"
            )

            every { loadProductOptionPort.findById(1) } returns productOption
            every { saveProductOptionPort.save(any()) } returns productOption

            val result = productOptionService.updateInfo(
                id = 1,
                name = "Titanium Black 256GB",
                description = "Titanium Black color with 256GB storage"
            )

            result.name shouldBe "Titanium Black 256GB"
            result.description shouldBe "Titanium Black color with 256GB storage"
            verify { saveProductOptionPort.save(productOption) }
        }

        it("should throw exception when product option not found") {
            every { loadProductOptionPort.findById(1) } returns null

            val exception = shouldThrow<IllegalArgumentException> {
                productOptionService.updateInfo(
                    id = 1,
                    name = "Titanium Black 256GB",
                    description = "Titanium Black color with 256GB storage"
                )
            }

            exception.message shouldBe "Product option not found: 1"
        }
    }
})
