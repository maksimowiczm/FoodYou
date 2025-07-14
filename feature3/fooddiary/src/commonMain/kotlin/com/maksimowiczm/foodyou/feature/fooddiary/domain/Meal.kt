package com.maksimowiczm.foodyou.feature.fooddiary.domain

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

    val energy: Float
        get() = food.map { it.food.nutritionFacts.energy }.sum().value ?: 0f

    val proteins: Float
        get() = food.map { it.food.nutritionFacts.proteins }.sum().value ?: 0f

    val carbohydrates: Float
        get() = food.map { it.food.nutritionFacts.carbohydrates }.sum().value ?: 0f

    val fats: Float
        get() = food.map { it.food.nutritionFacts.fats }.sum().value ?: 0f
}
