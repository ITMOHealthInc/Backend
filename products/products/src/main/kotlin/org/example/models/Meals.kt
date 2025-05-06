package org.example.models

import org.example.enums.TypesMeals
import java.time.LocalDateTime

data class Meals(
    val id: Long,
    val type: TypesMeals,
    val addedAt: LocalDateTime,
    val username: String
) 