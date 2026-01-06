package com.helloworld.commerce.product.api

import com.helloworld.commerce.product.api.dto.CreateProductOptionRequest
import com.helloworld.commerce.product.api.dto.UpdateProductOptionRequest
import com.helloworld.commerce.product.api.dto.UpdateStockRequest
import com.helloworld.commerce.product.application.port.input.CreateProductOptionUseCase
import com.helloworld.commerce.product.application.port.input.GetProductOptionUseCase
import com.helloworld.commerce.product.application.port.input.UpdateProductOptionUseCase
import com.helloworld.commerce.product.application.port.input.UpdateProductStockUseCase
import com.helloworld.commerce.product.application.port.output.LoadInventoryPort
import com.helloworld.commerce.product.domain.Inventory
import com.helloworld.commerce.product.domain.ProductOption
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ApplyExtension(SpringExtension::class)
@WebMvcTest(controllers = [ProductOptionController::class])
@ContextConfiguration(classes = [ProductOptionController::class])
class ProductOptionControllerSpec : DescribeSpec() {
    @MockkBean
    lateinit var createProductOptionUseCase: CreateProductOptionUseCase

    @MockkBean
    lateinit var getProductOptionUseCase: GetProductOptionUseCase

    @MockkBean
    lateinit var updateProductOptionUseCase: UpdateProductOptionUseCase

    @MockkBean
    lateinit var updateProductStockUseCase: UpdateProductStockUseCase

    @MockkBean
    lateinit var loadInventoryPort: LoadInventoryPort

    @Autowired
    lateinit var mockMvc: MockMvc

    init {
        describe("createProductOption") {
            it("should create product option successfully") {
                val request = CreateProductOptionRequest(
                    productId = 1,
                    name = "Black 256GB",
                    description = "Black color with 256GB storage",
                    initialStock = 100
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

                every {
                    createProductOptionUseCase.create(
                        productId = 1,
                        name = "Black 256GB",
                        description = "Black color with 256GB storage",
                        initialStock = 100
                    )
                } returns productOption
                every { loadInventoryPort.findByProductOptionId(1) } returns inventory

                mockMvc.perform(
                    post("/api/product-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                        {
                            "productId": 1,
                            "name": "Black 256GB",
                            "description": "Black color with 256GB storage",
                            "initialStock": 100
                        }
                        """.trimIndent()
                        )
                )
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.productId").value(1))
                    .andExpect(jsonPath("$.name").value("Black 256GB"))
                    .andExpect(jsonPath("$.stockQuantity").value(100))
            }
        }

        describe("getProductOption") {
            it("should return product option when exists") {
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

                every { getProductOptionUseCase.findById(1) } returns productOption
                every { loadInventoryPort.findByProductOptionId(1) } returns inventory

                mockMvc.perform(get("/api/product-options/1"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.productId").value(1))
                    .andExpect(jsonPath("$.name").value("Black 256GB"))
                    .andExpect(jsonPath("$.stockQuantity").value(100))
            }

            it("should return 404 when product option not found") {
                every { getProductOptionUseCase.findById(1) } returns null

                mockMvc.perform(get("/api/product-options/1"))
                    .andExpect(status().isNotFound)
            }
        }

        describe("getProductOptionsByProductId") {
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

                every { getProductOptionUseCase.findByProductId(1) } returns productOptions
                every { loadInventoryPort.findByProductOptionId(1) } returns Inventory(
                    id = 1,
                    productOptionId = 1,
                    quantity = 100
                )
                every { loadInventoryPort.findByProductOptionId(2) } returns Inventory(
                    id = 2,
                    productOptionId = 2,
                    quantity = 150
                )

                mockMvc.perform(get("/api/product-options/product/1"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$[0].name").value("Black 256GB"))
                    .andExpect(jsonPath("$[0].stockQuantity").value(100))
                    .andExpect(jsonPath("$[1].name").value("White 256GB"))
                    .andExpect(jsonPath("$[1].stockQuantity").value(150))
            }
        }

        describe("updateProductOption") {
            it("should update product option info successfully") {
                val request = UpdateProductOptionRequest(
                    name = "Titanium Black 256GB",
                    description = "Titanium Black color with 256GB storage"
                )
                val productOption = ProductOption(
                    id = 1,
                    productId = 1,
                    name = "Titanium Black 256GB",
                    description = "Titanium Black color with 256GB storage"
                )
                val inventory = Inventory(
                    id = 1,
                    productOptionId = 1,
                    quantity = 100
                )

                every {
                    updateProductOptionUseCase.updateInfo(
                        id = 1,
                        name = "Titanium Black 256GB",
                        description = "Titanium Black color with 256GB storage"
                    )
                } returns productOption
                every { loadInventoryPort.findByProductOptionId(1) } returns inventory

                mockMvc.perform(
                    put("/api/product-options/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                        {
                            "name": "Titanium Black 256GB",
                            "description": "Titanium Black color with 256GB storage"
                        }
                        """.trimIndent()
                        )
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.productId").value(1))
                    .andExpect(jsonPath("$.name").value("Titanium Black 256GB"))
                    .andExpect(jsonPath("$.description").value("Titanium Black color with 256GB storage"))
            }
        }

        describe("increaseStock") {
            it("should increase stock successfully") {
                val request = UpdateStockRequest(
                    amount = 10
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
                    quantity = 110
                )

                every {
                    updateProductStockUseCase.increase(
                        productOptionId = 1,
                        amount = 10
                    )
                } answers {}
                every { getProductOptionUseCase.findById(1) } returns productOption
                every { loadInventoryPort.findByProductOptionId(1) } returns inventory

                mockMvc.perform(
                    put("/api/product-options/1/stock/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                        {
                            "amount": 10
                        }
                        """.trimIndent()
                        )
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.stockQuantity").value(110))
            }
        }

        describe("decreaseStock") {
            it("should decrease stock successfully") {
                val request = UpdateStockRequest(
                    amount = 10
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
                    quantity = 90
                )

                every {
                    updateProductStockUseCase.decrease(
                        productOptionId = 1,
                        amount = 10
                    )
                } answers {}
                every { getProductOptionUseCase.findById(1) } returns productOption
                every { loadInventoryPort.findByProductOptionId(1) } returns inventory

                mockMvc.perform(
                    put("/api/product-options/1/stock/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                        {
                            "amount": 10
                        }
                        """.trimIndent()
                        )
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.stockQuantity").value(90))
            }
        }
    }
}
