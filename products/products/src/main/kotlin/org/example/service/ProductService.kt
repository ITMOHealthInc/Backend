package org.example.service

import org.example.enums.Affiliation
import org.example.models.Product
import org.example.models.UserProduct
import org.example.repository.ProductRepository
import org.example.repository.UserProductRepository
import org.example.repository.UserRepository

class ProductService(
    private val repository: ProductRepository,
    private val userProductRepository: UserProductRepository,
    private val userRepository: UserRepository = UserRepository()
) {

    fun createProduct(product: Product, username: String? = null): Product {
        require(product.mass != null && product.mass > 0) { "Масса продукта должна быть больше 0" }
        require(product.water != null && product.water >= 0) { "Содержание воды не может быть отрицательным" }
        
        if (username != null && !userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        
        val createdProduct = repository.insert(product)
        
        if (product.affiliation == Affiliation.USER && username != null) {
            userProductRepository.insert(UserProduct(username, createdProduct.id!!))
        }
        
        return createdProduct
    }

    fun getProduct(id: Long, username: String? = null): Product? {
        val product = repository.findById(id) ?: return null
        
        if (product.affiliation == Affiliation.USER) {
            if (username == null) {
                throw SecurityException("Username is required for user products")
            }
            if (!userRepository.exists(username)) {
                throw IllegalArgumentException("User with username $username does not exist")
            }
            val productOwner = userProductRepository.findByProductId(id)
                ?: throw SecurityException("Product $id is marked as USER but has no owner")
            if (productOwner != username) {
                throw SecurityException("You don't have permission to access this product")
            }
        }
        
        return product
    }

    fun getAllProducts(username: String? = null): List<Product> {
        val allProducts = repository.findAll()
        if (username == null) {
            return allProducts.filter { it.affiliation != Affiliation.USER }
        }
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        
        val userProductIds = userProductRepository.findByUsername(username)
        return allProducts.filter { 
            it.affiliation != Affiliation.USER || 
            (it.affiliation == Affiliation.USER && it.id in userProductIds)
        }
    }

    fun getProductsByUsername(username: String): List<Product> {
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        
        val productIds = userProductRepository.findByUsername(username)
        return productIds.mapNotNull { repository.findById(it) }
    }

    fun deleteProduct(id: Long, username: String? = null): Boolean {
        val product = repository.findById(id) ?: return false
        
        if (product.affiliation == Affiliation.USER) {
            if (username == null) {
                throw SecurityException("Username is required for user products")
            }
            if (!userRepository.exists(username)) {
                throw IllegalArgumentException("User with username $username does not exist")
            }
            val productOwner = userProductRepository.findByProductId(id)
                ?: throw SecurityException("Product $id is marked as USER but has no owner")
            if (productOwner != username) {
                throw SecurityException("You don't have permission to delete this product")
            }
            userProductRepository.deleteByProductId(id)
        }
        
        return repository.deleteById(id)
    }

    fun updateProduct(product: Product, username: String? = null): Product? {
        require(product.id != null) { "Product ID must not be null for update" }
        require(product.mass != null && product.mass > 0) { "Масса продукта должна быть больше 0" }
        require(product.water != null && product.water >= 0) { "Содержание воды не может быть отрицательным" }
        
        val existingProduct = repository.findById(product.id)
            ?: throw IllegalArgumentException("Product with id ${product.id} not found")
        
        if (existingProduct.affiliation == Affiliation.USER) {
            if (username == null) {
                throw SecurityException("Username is required for user products")
            }
            if (!userRepository.exists(username)) {
                throw IllegalArgumentException("User with username $username does not exist")
            }
            val productOwner = userProductRepository.findByProductId(product.id)
                ?: throw SecurityException("Product ${product.id} is marked as USER but has no owner")
            if (productOwner != username) {
                throw SecurityException("You don't have permission to update this product")
            }
        }
        
        if (existingProduct.affiliation != Affiliation.USER && product.affiliation == Affiliation.USER) {
            throw IllegalArgumentException("Нельзя изменить принадлежность продукта с региональной на USER")
        }
        
        if (existingProduct.affiliation == Affiliation.USER && product.affiliation != Affiliation.USER) {
            userProductRepository.deleteByProductId(product.id)
        }
        
        return if (repository.update(product)) {
            repository.findById(product.id)
        } else {
            null
        }
    }
}

