package com.maksimowiczm.foodyou.feature.meal.domain

import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.ext.sumOf
import kotlinx.datetime.LocalTime

internal data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int,
    val food: List<FoodWithMeasurement>
) {
    val isAllDay: Boolean
        get() = from == to

    val calories: Float = food
        .sumOf { it.food.nutritionFacts.calories.value * (it.weight ?: 0f) / 100f }

    val proteins: Float = food
        .sumOf { it.food.nutritionFacts.proteins.value * (it.weight ?: 0f) / 100f }

    val carbohydrates: Float = food
        .sumOf { it.food.nutritionFacts.carbohydrates.value * (it.weight ?: 0f) / 100f }

    val fats: Float = food
        .sumOf { it.food.nutritionFacts.fats.value * (it.weight ?: 0f) / 100f }
}
