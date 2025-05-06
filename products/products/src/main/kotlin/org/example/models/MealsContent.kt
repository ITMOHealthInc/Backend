package org.example.models

import org.example.enums.TypesMealsContent

data class MealsContent(
    val mealId: Long,
    val contentId: Long,
    val typeContent: TypesMealsContent,
) 