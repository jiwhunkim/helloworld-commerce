package com.helloworld.commerce.product.application.service

import com.helloworld.commerce.product.application.port.output.LoadInventoryPort
import com.helloworld.commerce.product.application.port.output.SaveInventoryPort
import com.helloworld.commerce.product.domain.Inventory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class InventoryServiceSpec : DescribeSpec({
    val loadInventoryPort = mockk<LoadInventoryPort>()
    val saveInventoryPort = mockk<SaveInventoryPort>()
    val inventoryService = InventoryService(loadInventoryPort, saveInventoryPort)

    describe("increase") {
        it("should increase stock successfully") {
            val inventory = Inventory(
                id = 1,
                productOptionId = 1,
                quantity = 100
            )

            every { loadInventoryPort.findByProductOptionId(1) } returns inventory
            every { saveInventoryPort.save(any()) } returns inventory

            inventoryService.increase(
                productOptionId = 1,
                amount = 10
            )

            inventory.quantity shouldBe 110
            verify { saveInventoryPort.save(inventory) }
        }

        it("should throw exception when inventory not found") {
            every { loadInventoryPort.findByProductOptionId(1) } returns null

            val exception = shouldThrow<IllegalArgumentException> {
                inventoryService.increase(
                    productOptionId = 1,
                    amount = 10
                )
            }

            exception.message shouldBe "Inventory not found for product option: 1"
        }

        it("should throw exception when amount is not positive") {
            val inventory = Inventory(
                id = 1,
                productOptionId = 1,
                quantity = 100
            )

            every { loadInventoryPort.findByProductOptionId(1) } returns inventory

            val exception = shouldThrow<IllegalArgumentException> {
                inventoryService.increase(
                    productOptionId = 1,
                    amount = 0
                )
            }

            exception.message shouldBe "Increase amount must be positive"
        }
    }

    describe("decrease") {
        it("should decrease stock successfully") {
            val inventory = Inventory(
                id = 1,
                productOptionId = 1,
                quantity = 100
            )

            every { loadInventoryPort.findByProductOptionId(1) } returns inventory
            every { saveInventoryPort.save(any()) } returns inventory

            inventoryService.decrease(
                productOptionId = 1,
                amount = 10
            )

            inventory.quantity shouldBe 90
            verify { saveInventoryPort.save(inventory) }
        }

        it("should throw exception when inventory not found") {
            every { loadInventoryPort.findByProductOptionId(1) } returns null

            val exception = shouldThrow<IllegalArgumentException> {
                inventoryService.decrease(
                    productOptionId = 1,
                    amount = 10
                )
            }

            exception.message shouldBe "Inventory not found for product option: 1"
        }

        it("should throw exception when amount is not positive") {
            val inventory = Inventory(
                id = 1,
                productOptionId = 1,
                quantity = 100
            )

            every { loadInventoryPort.findByProductOptionId(1) } returns inventory

            val exception = shouldThrow<IllegalArgumentException> {
                inventoryService.decrease(
                    productOptionId = 1,
                    amount = 0
                )
            }

            exception.message shouldBe "Decrease amount must be positive"
        }

        it("should throw exception when insufficient stock") {
            val inventory = Inventory(
                id = 1,
                productOptionId = 1,
                quantity = 5
            )

            every { loadInventoryPort.findByProductOptionId(1) } returns inventory

            val exception = shouldThrow<IllegalArgumentException> {
                inventoryService.decrease(
                    productOptionId = 1,
                    amount = 10
                )
            }

            exception.message shouldBe "Insufficient stock. Available: 5, Requested: 10"
        }
    }
})
