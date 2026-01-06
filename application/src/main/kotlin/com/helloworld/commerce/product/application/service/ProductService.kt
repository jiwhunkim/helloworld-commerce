package com.helloworld.commerce.product.application.service

import com.helloworld.commerce.product.application.port.input.CreateProductUseCase
import com.helloworld.commerce.product.application.port.input.GetProductUseCase
import com.helloworld.commerce.product.application.port.input.UpdateProductUseCase
import com.helloworld.commerce.product.application.port.output.LoadProductPort
import com.helloworld.commerce.product.application.port.output.SaveProductPort
import com.helloworld.commerce.product.domain.Product
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

private val log = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class ProductService(
    private val loadProductPort: LoadProductPort,
    private val saveProductPort: SaveProductPort
) : CreateProductUseCase, GetProductUseCase, UpdateProductUseCase {

    @Transactional
    override fun create(name: String, description: String, price: BigDecimal, stockQuantity: Int): Product {
        log.info { "Creating product: name=$name, price=$price" }

        val product = Product(
            name = name,
            description = description,
            price = price
        )

        val saved = saveProductPort.save(product)

        log.info { "Product created: id=${saved.id}" }
        return saved
    }

    override fun getById(id: Long): Product? {
        log.debug { "Getting product by id: $id" }
        return loadProductPort.findById(id)
    }

    override fun getAll(): List<Product> {
        log.debug { "Getting all products" }
        return loadProductPort.findAll()
    }

    override fun searchByName(name: String): List<Product> {
        log.debug { "Searching products by name: $name" }
        return loadProductPort.findByNameContaining(name)
    }

    @Transactional
    override fun updateInfo(productId: Long, name: String, description: String): Product {
        log.info { "Updating product info: productId=$productId" }

        val product = loadProductPort.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        product.updateInfo(name, description)
        val saved = saveProductPort.save(product)

        log.info { "Product info updated: productId=${saved.id}" }
        return saved
    }

    @Transactional
    override fun updatePrice(productId: Long, price: BigDecimal): Product {
        log.info { "Updating product price: productId=$productId, newPrice=$price" }

        val product = loadProductPort.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        product.updatePrice(price)
        val saved = saveProductPort.save(product)

        log.info { "Product price updated: productId=${saved.id}, newPrice=${saved.price}" }
        return saved
    }
}
