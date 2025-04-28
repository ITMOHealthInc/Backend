package org.example.mappers

import org.example.dto.RecipeDTO
import org.example.dto.RecipeProductDTO
import org.example.dto.RecipeRequestDTO
import org.example.models.Recipe
import org.example.models.RecipeProduct
import org.example.models.Product

fun Recipe.toDTO(products: List<Product>): RecipeDTO {
    return RecipeDTO(
        id = id,
        name = name,
        username = username,
        products = products.map { it.toDTO() }
    )
}

fun RecipeProduct.toDTO(): RecipeProductDTO {
    return RecipeProductDTO(
        recipeId = recipeId,
        productId = productId
    )
}

fun RecipeRequestDTO.toEntity(username: String): Recipe {
    return Recipe(
        id = null,
        name = name,
        username = username
    )
}

fun RecipeProductDTO.toEntity(): RecipeProduct {
    return RecipeProduct(
        recipeId = recipeId,
        productId = productId
    )
} 