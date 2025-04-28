package org.example.service

import org.example.enums.Affiliation
import org.example.models.Product
import org.example.models.UserProduct
import org.example.repository.ProductRepository
import org.example.repository.UserProductRepository

class ProductService(
    private val repository: ProductRepository,
    private val userProductRepository: UserProductRepository
) {

    fun createProduct(product: Product, username: String? = null): Product {
        require(product.mass != null && product.mass > 0) { "Масса продукта должна быть больше 0" }
        require(product.water != null && product.water >= 0) { "Содержание воды не может быть отрицательным" }
        
        val createdProduct = repository.insert(product)
        
        if (product.affiliation == Affiliation.USER && username != null) {
            userProductRepository.insert(UserProduct(username, createdProduct.id!!))
        }
        
        return createdProduct
    }

    fun getProduct(id: Long): Product? {
        return repository.findById(id)
    }

    fun getAllProducts(): List<Product> {
        return repository.findAll()
    }

    fun getProductsByUsername(username: String): List<Product> {
        val productIds = userProductRepository.findByUsername(username)
        return productIds.mapNotNull { repository.findById(it) }
    }

    fun deleteProduct(id: Long): Boolean {
        val product = repository.findById(id)
        if (product?.affiliation == Affiliation.USER) {
            userProductRepository.deleteByProductId(id)
        }
        return repository.deleteById(id)
    }

    fun updateProduct(product: Product): Product? {
        require(product.id != null) { "Product ID must not be null for update" }
        require(product.mass != null && product.mass > 0) { "Масса продукта должна быть больше 0" }
        require(product.water != null && product.water >= 0) { "Содержание воды не может быть отрицательным" }
        
        val existingProduct = repository.findById(product.id)
        
        if (existingProduct?.affiliation != Affiliation.USER && product.affiliation == Affiliation.USER) {
            throw IllegalArgumentException("Нельзя изменить принадлежность продукта с региональной на USER")
        }
        
        if (existingProduct?.affiliation == Affiliation.USER && product.affiliation != Affiliation.USER) {
            userProductRepository.deleteByProductId(product.id)
        }
        
        return if (repository.update(product)) {
            repository.findById(product.id)
        } else {
            null
        }
    }
}

