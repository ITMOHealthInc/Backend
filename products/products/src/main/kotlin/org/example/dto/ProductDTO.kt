package org.example.dto

import kotlinx.serialization.Serializable
import org.example.enums.Affiliation

@Serializable
data class ProductDTO(
    val id: Long? = null,
    val name: String,
    val affiliation: Affiliation,
    val water: Double,
    val mass: Double,

    val fiber: Double,
    val sugar: Double,
    val addedSugar: Double? = null,

    val saturatedFat: Double,
    val polyunsaturatedFat: Double? = null,

    val cholesterol: Double? = null,
    val salt: Double,
    val alcohol: Double? = null,

    val vitaminB7: Double? = null,
    val vitaminC: Double? = null,
    val vitaminD: Double? = null,
    val vitaminE: Double? = null,
    val vitaminK: Double? = null,

    val calcium: Double? = null,
    val iron: Double? = null,
    val zinc: Double? = null
)
