package com.helloworld.commerce.product.application.service

import com.helloworld.commerce.product.application.port.output.LoadProductPort
import com.helloworld.commerce.product.application.port.output.SaveProductPort
import com.helloworld.commerce.product.domain.Product
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal

class ProductServiceSpec : DescribeSpec({
    val loadProductPort = mockk<LoadProductPort>()
    val saveProductPort = mockk<SaveProductPort>()
    val productService = ProductService(loadProductPort, saveProductPort)

    describe("create") {
        it("should create product successfully") {
            val product = Product(
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1299000")
            )

            every { saveProductPort.save(any()) } returns product

            val result = productService.create(
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1299000"),
                stockQuantity = 0
            )

            result.name shouldBe "iPhone 15 Pro"
            result.description shouldBe "Apple iPhone 15 Pro"
            result.price shouldBe BigDecimal("1299000")
            verify { saveProductPort.save(any()) }
        }
    }

    describe("getById") {
        it("should return product when exists") {
            val product = Product(
                id = 1,
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1299000")
            )

            every { loadProductPort.findById(1) } returns product

            val result = productService.getById(1)

            result?.id shouldBe 1
            result?.name shouldBe "iPhone 15 Pro"
        }

        it("should return null when product does not exist") {
            every { loadProductPort.findById(1) } returns null

            val result = productService.getById(1)

            result shouldBe null
        }
    }

    describe("getAll") {
        it("should return all products") {
            val products = listOf(
                Product(
                    id = 1,
                    name = "iPhone 15 Pro",
                    description = "Apple iPhone 15 Pro",
                    price = BigDecimal("1299000")
                ),
                Product(
                    id = 2,
                    name = "MacBook Pro",
                    description = "Apple MacBook Pro",
                    price = BigDecimal("3299000")
                )
            )

            every { loadProductPort.findAll() } returns products

            val result = productService.getAll()

            result.size shouldBe 2
            result[0].name shouldBe "iPhone 15 Pro"
            result[1].name shouldBe "MacBook Pro"
        }
    }

    describe("searchByName") {
        it("should return products matching name") {
            val products = listOf(
                Product(
                    id = 1,
                    name = "iPhone 15 Pro",
                    description = "Apple iPhone 15 Pro",
                    price = BigDecimal("1299000")
                )
            )

            every { loadProductPort.findByNameContaining("iPhone") } returns products

            val result = productService.searchByName("iPhone")

            result.size shouldBe 1
            result[0].name shouldBe "iPhone 15 Pro"
        }
    }

    describe("updateInfo") {
        it("should update product info successfully") {
            val product = Product(
                id = 1,
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1299000")
            )

            every { loadProductPort.findById(1) } returns product
            every { saveProductPort.save(any()) } returns product

            val result = productService.updateInfo(
                productId = 1,
                name = "iPhone 15 Pro Max",
                description = "Apple iPhone 15 Pro Max"
            )

            result.name shouldBe "iPhone 15 Pro Max"
            result.description shouldBe "Apple iPhone 15 Pro Max"
            verify { saveProductPort.save(product) }
        }

        it("should throw exception when product not found") {
            every { loadProductPort.findById(1) } returns null

            val exception = shouldThrow<IllegalArgumentException> {
                productService.updateInfo(
                    productId = 1,
                    name = "iPhone 15 Pro Max",
                    description = "Apple iPhone 15 Pro Max"
                )
            }

            exception.message shouldBe "Product not found: 1"
        }
    }

    describe("updatePrice") {
        it("should update product price successfully") {
            val product = Product(
                id = 1,
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1299000")
            )

            every { loadProductPort.findById(1) } returns product
            every { saveProductPort.save(any()) } returns product

            val result = productService.updatePrice(
                productId = 1,
                price = BigDecimal("1499000")
            )

            result.price shouldBe BigDecimal("1499000")
            verify { saveProductPort.save(product) }
        }

        it("should throw exception when product not found") {
            every { loadProductPort.findById(1) } returns null

            val exception = shouldThrow<IllegalArgumentException> {
                productService.updatePrice(
                    productId = 1,
                    price = BigDecimal("1499000")
                )
            }

            exception.message shouldBe "Product not found: 1"
        }
    }
})
