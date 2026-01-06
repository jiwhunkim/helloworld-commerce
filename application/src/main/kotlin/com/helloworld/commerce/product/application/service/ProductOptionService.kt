package com.helloworld.commerce.product.application.service

import com.helloworld.commerce.product.application.port.input.CreateProductOptionUseCase
import com.helloworld.commerce.product.application.port.input.GetProductOptionUseCase
import com.helloworld.commerce.product.application.port.input.UpdateProductOptionUseCase
import com.helloworld.commerce.product.application.port.output.LoadInventoryPort
import com.helloworld.commerce.product.application.port.output.LoadProductOptionPort
import com.helloworld.commerce.product.application.port.output.LoadProductPort
import com.helloworld.commerce.product.application.port.output.SaveInventoryPort
import com.helloworld.commerce.product.application.port.output.SaveProductOptionPort
import com.helloworld.commerce.product.domain.Inventory
import com.helloworld.commerce.product.domain.ProductOption
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class ProductOptionService(
    private val loadProductOptionPort: LoadProductOptionPort,
    private val saveProductOptionPort: SaveProductOptionPort,
    private val loadProductPort: LoadProductPort,
    private val loadInventoryPort: LoadInventoryPort,
    private val saveInventoryPort: SaveInventoryPort
) : CreateProductOptionUseCase, GetProductOptionUseCase, UpdateProductOptionUseCase {

    @Transactional
    override fun create(productId: Long, name: String, description: String, initialStock: Int): ProductOption {
        log.info { "Creating product option: productId=$productId, name=$name, initialStock=$initialStock" }

        loadProductPort.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        val productOption = ProductOption(
            productId = productId,
            name = name,
            description = description
        )

        val savedOption = saveProductOptionPort.save(productOption)

        val inventory = Inventory(
            productOptionId = savedOption.id!!,
            quantity = initialStock
        )

        saveInventoryPort.save(inventory)

        log.info { "Product option created: id=${savedOption.id}" }
        return savedOption
    }

    override fun findById(id: Long): ProductOption? {
        log.debug { "Getting product option by id: $id" }
        return loadProductOptionPort.findById(id)
    }

    override fun findByProductId(productId: Long): List<ProductOption> {
        log.debug { "Getting product options by productId: $productId" }
        return loadProductOptionPort.findByProductId(productId)
    }

    @Transactional
    override fun updateInfo(id: Long, name: String, description: String): ProductOption {
        log.info { "Updating product option info: id=$id" }

        val productOption = loadProductOptionPort.findById(id)
            ?: throw IllegalArgumentException("Product option not found: $id")

        productOption.updateInfo(name, description)
        val saved = saveProductOptionPort.save(productOption)

        log.info { "Product option info updated: id=${saved.id}" }
        return saved
    }
}
