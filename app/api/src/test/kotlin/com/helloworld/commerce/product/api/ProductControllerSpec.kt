package com.helloworld.commerce.product.api

import com.helloworld.commerce.product.api.dto.CreateProductRequest
import com.helloworld.commerce.product.api.dto.UpdateProductInfoRequest
import com.helloworld.commerce.product.api.dto.UpdateProductPriceRequest
import com.helloworld.commerce.product.api.dto.ProductResponse
import com.helloworld.commerce.product.application.port.input.CreateProductUseCase
import com.helloworld.commerce.product.application.port.input.GetProductUseCase
import com.helloworld.commerce.product.application.port.input.UpdateProductUseCase
import com.helloworld.commerce.product.domain.Product
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal

class ProductControllerSpec : DescribeSpec({
    val createProductUseCase = mockk<CreateProductUseCase>()
    val getProductUseCase = mockk<GetProductUseCase>()
    val updateProductUseCase = mockk<UpdateProductUseCase>()
    val productController = ProductController(
        createProductUseCase,
        getProductUseCase,
        updateProductUseCase
    )

    describe("createProduct") {
        it("should create product successfully") {
            val request = CreateProductRequest(
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1299000")
            )
            val product = Product(
                id = 1,
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1299000")
            )

            every {
                createProductUseCase.create(
                    name = "iPhone 15 Pro",
                    description = "Apple iPhone 15 Pro",
                    price = BigDecimal("1299000"),
                    stockQuantity = 0
                )
            } returns product

            val response = productController.createProduct(request)

            response.statusCode shouldBe org.springframework.http.HttpStatus.CREATED
            val body = response.body as ProductResponse
            body.id shouldBe 1
            body.name shouldBe "iPhone 15 Pro"
            verify { createProductUseCase.create(any(), any(), any(), any()) }
        }
    }

    describe("getProduct") {
        it("should return product when exists") {
            val product = Product(
                id = 1,
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1299000")
            )

            every { getProductUseCase.getById(1) } returns product

            val response = productController.getProduct(1)

            response.statusCode shouldBe org.springframework.http.HttpStatus.OK
            val body = response.body as ProductResponse
            body.id shouldBe 1
            body.name shouldBe "iPhone 15 Pro"
        }

        it("should return 404 when product not found") {
            every { getProductUseCase.getById(1) } returns null

            val response = productController.getProduct(1)

            response.statusCode shouldBe org.springframework.http.HttpStatus.NOT_FOUND
        }
    }

    describe("getAllProducts") {
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

            every { getProductUseCase.getAll() } returns products

            val response = productController.getAllProducts()

            response.statusCode shouldBe org.springframework.http.HttpStatus.OK
            val body = response.body!!
            body.size shouldBe 2
            body[0].name shouldBe "iPhone 15 Pro"
            body[1].name shouldBe "MacBook Pro"
        }
    }

    describe("searchProducts") {
        it("should return products matching name") {
            val products = listOf(
                Product(
                    id = 1,
                    name = "iPhone 15 Pro",
                    description = "Apple iPhone 15 Pro",
                    price = BigDecimal("1299000")
                )
            )

            every { getProductUseCase.searchByName("iPhone") } returns products

            val response = productController.searchProducts("iPhone")

            response.statusCode shouldBe org.springframework.http.HttpStatus.OK
            val body = response.body!!
            body.size shouldBe 1
            body[0].name shouldBe "iPhone 15 Pro"
        }
    }

    describe("updateProductInfo") {
        it("should update product info successfully") {
            val request = UpdateProductInfoRequest(
                name = "iPhone 15 Pro Max",
                description = "Apple iPhone 15 Pro Max"
            )
            val product = Product(
                id = 1,
                name = "iPhone 15 Pro Max",
                description = "Apple iPhone 15 Pro Max",
                price = BigDecimal("1299000")
            )

            every {
                updateProductUseCase.updateInfo(
                    productId = 1,
                    name = "iPhone 15 Pro Max",
                    description = "Apple iPhone 15 Pro Max"
                )
            } returns product

            val response = productController.updateProductInfo(1, request)

            response.statusCode shouldBe org.springframework.http.HttpStatus.OK
            val body = response.body as ProductResponse
            body.name shouldBe "iPhone 15 Pro Max"
            verify { updateProductUseCase.updateInfo(any(), any(), any()) }
        }
    }

    describe("updateProductPrice") {
        it("should update product price successfully") {
            val request = UpdateProductPriceRequest(
                price = BigDecimal("1499000")
            )
            val product = Product(
                id = 1,
                name = "iPhone 15 Pro",
                description = "Apple iPhone 15 Pro",
                price = BigDecimal("1499000")
            )

            every {
                updateProductUseCase.updatePrice(
                    productId = 1,
                    price = BigDecimal("1499000")
                )
            } returns product

            val response = productController.updateProductPrice(1, request)

            response.statusCode shouldBe org.springframework.http.HttpStatus.OK
            val body = response.body as ProductResponse
            body.price shouldBe BigDecimal("1499000")
            verify { updateProductUseCase.updatePrice(any(), any()) }
        }
    }
})
