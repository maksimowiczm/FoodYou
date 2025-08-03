package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.feature.food.domain.sum
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

    val energy: Float by lazy {
        food.mapNotNull { it.energy }.sum()
    }

    val proteins: Float by lazy {
        food.mapNotNull { it.proteins }.sum()
    }

    val carbohydrates: Float by lazy {
        food.mapNotNull { it.carbohydrates }.sum()
    }

    val fats: Float by lazy {
        food.mapNotNull { it.fats }.sum()
    }

    val nutritionFacts: NutritionFacts by lazy {
        food.mapNotNull { it.nutritionFacts }.sum()
    }
}
