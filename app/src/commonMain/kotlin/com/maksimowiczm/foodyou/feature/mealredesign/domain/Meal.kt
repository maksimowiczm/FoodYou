package com.maksimowiczm.foodyou.feature.mealredesign.domain

import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.ext.sumOf
import kotlin.math.roundToInt
import kotlinx.datetime.LocalTime

data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int,
    val food: List<FoodWithMeasurement>
) {
    val isAllDay: Boolean
        get() = from == to

    val calories: Int = food
        .sumOf { it.food.nutritionFacts.calories.value * (it.weight ?: 0f) / 100f }
        .roundToInt()

    val proteins: Int = food
        .sumOf { it.food.nutritionFacts.proteins.value * (it.weight ?: 0f) / 100f }
        .roundToInt()

    val carbohydrates: Int = food
        .sumOf { it.food.nutritionFacts.carbohydrates.value * (it.weight ?: 0f) / 100f }
        .roundToInt()

    val fats: Int = food
        .sumOf { it.food.nutritionFacts.fats.value * (it.weight ?: 0f) / 100f }
        .roundToInt()
}
