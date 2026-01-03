package com.helloworld.commerce.api

import com.helloworld.commerce.application.ProductService
import com.helloworld.commerce.domain.Product
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.findAll()
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.findById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ProductResponse.from(product))
    }

    @GetMapping("/search")
    fun searchProducts(@RequestParam name: String): ResponseEntity<List<ProductResponse>> {
        val products = productService.searchByName(name)
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }

    @PostMapping
    fun createProduct(@RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.create(request.toDomain())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductResponse.from(product))
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductRequest
    ): ResponseEntity<ProductResponse> {
        val product = productService.update(id, request.toDomain())
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ProductResponse.from(product))
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        return if (productService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class ProductResponse(
    val id: Long?,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val stockQuantity: Int,
    val inStock: Boolean
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                stockQuantity = product.stockQuantity,
                inStock = product.isInStock()
            )
        }
    }
}

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val stockQuantity: Int
) {
    fun toDomain(): Product {
        return Product(
            name = name,
            description = description,
            price = price,
            stockQuantity = stockQuantity
        )
    }
}

data class UpdateProductRequest(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val stockQuantity: Int
) {
    fun toDomain(): Product {
        return Product(
            name = name,
            description = description,
            price = price,
            stockQuantity = stockQuantity
        )
    }
}
