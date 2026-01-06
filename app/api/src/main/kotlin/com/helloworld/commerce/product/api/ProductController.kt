package com.helloworld.commerce.product.api

import com.helloworld.commerce.product.api.dto.*
import com.helloworld.commerce.product.application.port.input.CreateProductUseCase
import com.helloworld.commerce.product.application.port.input.GetProductUseCase
import com.helloworld.commerce.product.application.port.input.UpdateProductUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val createProductUseCase: CreateProductUseCase,
    private val getProductUseCase: GetProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase
) {

    @PostMapping
    fun createProduct(@RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = createProductUseCase.create(
            name = request.name,
            description = request.description,
            price = request.price,
            stockQuantity = 0
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product))
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = getProductUseCase.getById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ProductResponse.from(product))
    }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = getProductUseCase.getAll()
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }

    @GetMapping("/search")
    fun searchProducts(@RequestParam name: String): ResponseEntity<List<ProductResponse>> {
        val products = getProductUseCase.searchByName(name)
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }

    @PutMapping("/{id}/info")
    fun updateProductInfo(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductInfoRequest
    ): ResponseEntity<ProductResponse> {
        val product = updateProductUseCase.updateInfo(id, request.name, request.description)
        return ResponseEntity.ok(ProductResponse.from(product))
    }

    @PutMapping("/{id}/price")
    fun updateProductPrice(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductPriceRequest
    ): ResponseEntity<ProductResponse> {
        val product = updateProductUseCase.updatePrice(id, request.price)
        return ResponseEntity.ok(ProductResponse.from(product))
    }
}
