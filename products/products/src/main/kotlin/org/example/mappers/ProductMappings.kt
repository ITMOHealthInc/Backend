package org.example.mappers

import org.example.dto.ProductDTO
import org.example.models.Product

fun ProductDTO.toEntity(): Product {
    return Product(
        id = id,
        name = name,
        affiliation = affiliation,
        water = water,
        mass = mass,
        fiber = fiber,
        sugar = sugar,
        addedSugar = addedSugar,
        saturatedFat = saturatedFat,
        polyunsaturatedFat = polyunsaturatedFat,
        cholesterol = cholesterol,
        salt = salt,
        alcohol = alcohol,
        vitaminB7 = vitaminB7,
        vitaminC = vitaminC,
        vitaminD = vitaminD,
        vitaminE = vitaminE,
        vitaminK = vitaminK,
        calcium = calcium,
        iron = iron,
        zinc = zinc
    )
}

fun Product.toDTO(): ProductDTO {
    return ProductDTO(
        id = id,
        name = name,
        affiliation = affiliation,
        water = water,
        mass = mass,
        fiber = fiber,
        sugar = sugar,
        addedSugar = addedSugar,
        saturatedFat = saturatedFat,
        polyunsaturatedFat = polyunsaturatedFat,
        cholesterol = cholesterol,
        salt = salt,
        alcohol = alcohol,
        vitaminB7 = vitaminB7,
        vitaminC = vitaminC,
        vitaminD = vitaminD,
        vitaminE = vitaminE,
        vitaminK = vitaminK,
        calcium = calcium,
        iron = iron,
        zinc = zinc
    )
}