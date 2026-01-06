package com.helloworld.commerce.product.api

import com.helloworld.commerce.product.api.dto.*
import com.helloworld.commerce.product.application.port.input.CreateProductOptionUseCase
import com.helloworld.commerce.product.application.port.input.GetProductOptionUseCase
import com.helloworld.commerce.product.application.port.input.UpdateProductOptionUseCase
import com.helloworld.commerce.product.application.port.input.UpdateProductStockUseCase
import com.helloworld.commerce.product.application.port.output.LoadInventoryPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/product-options")
class ProductOptionController(
    private val createProductOptionUseCase: CreateProductOptionUseCase,
    private val getProductOptionUseCase: GetProductOptionUseCase,
    private val updateProductOptionUseCase: UpdateProductOptionUseCase,
    private val updateProductStockUseCase: UpdateProductStockUseCase,
    private val loadInventoryPort: LoadInventoryPort
) {

    @PostMapping
    fun createProductOption(@RequestBody request: CreateProductOptionRequest): ResponseEntity<ProductOptionResponse> {
        val productOption = createProductOptionUseCase.create(
            productId = request.productId,
            name = request.name,
            description = request.description,
            initialStock = request.initialStock
        )
        val inventory = loadInventoryPort.findByProductOptionId(productOption.id!!)
            ?: throw IllegalStateException("Inventory not found for created product option")
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductOptionResponse.from(productOption, inventory.quantity))
    }

    @GetMapping("/{id}")
    fun getProductOption(@PathVariable id: Long): ResponseEntity<ProductOptionResponse> {
        val productOption = getProductOptionUseCase.findById(id)
            ?: return ResponseEntity.notFound().build()
        
        val inventory = loadInventoryPort.findByProductOptionId(id)
            ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(ProductOptionResponse.from(productOption, inventory.quantity))
    }

    @GetMapping("/product/{productId}")
    fun getProductOptionsByProductId(@PathVariable productId: Long): ResponseEntity<List<ProductOptionResponse>> {
        val productOptions = getProductOptionUseCase.findByProductId(productId)
        
        val responses = productOptions.map { option ->
            val inventory = loadInventoryPort.findByProductOptionId(option.id!!)
                ?: throw IllegalStateException("Inventory not found for product option: ${option.id}")
            ProductOptionResponse.from(option, inventory.quantity)
        }
        
        return ResponseEntity.ok(responses)
    }

    @PutMapping("/{id}")
    fun updateProductOption(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductOptionRequest
    ): ResponseEntity<ProductOptionResponse> {
        val productOption = updateProductOptionUseCase.updateInfo(id, request.name, request.description)
        
        val inventory = loadInventoryPort.findByProductOptionId(id)
            ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(ProductOptionResponse.from(productOption, inventory.quantity))
    }

    @PutMapping("/{id}/stock/increase")
    fun increaseStock(
        @PathVariable id: Long,
        @RequestBody request: UpdateStockRequest
    ): ResponseEntity<ProductOptionResponse> {
        updateProductStockUseCase.increase(id, request.amount)
        
        val productOption = getProductOptionUseCase.findById(id)
            ?: return ResponseEntity.notFound().build()
        
        val inventory = loadInventoryPort.findByProductOptionId(id)
            ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(ProductOptionResponse.from(productOption, inventory.quantity))
    }

    @PutMapping("/{id}/stock/decrease")
    fun decreaseStock(
        @PathVariable id: Long,
        @RequestBody request: UpdateStockRequest
    ): ResponseEntity<ProductOptionResponse> {
        updateProductStockUseCase.decrease(id, request.amount)
        
        val productOption = getProductOptionUseCase.findById(id)
            ?: return ResponseEntity.notFound().build()
        
        val inventory = loadInventoryPort.findByProductOptionId(id)
            ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(ProductOptionResponse.from(productOption, inventory.quantity))
    }
}
