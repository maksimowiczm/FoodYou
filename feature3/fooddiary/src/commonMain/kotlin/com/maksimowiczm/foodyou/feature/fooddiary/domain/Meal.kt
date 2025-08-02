package com.maksimowiczm.foodyou.feature.fooddiary.domain

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
        get() = food.mapNotNull { it.energy }.sum()

    val proteins: Float
        get() = food.mapNotNull { it.proteins }.sum()

    val carbohydrates: Float
        get() = food.mapNotNull { it.carbohydrates }.sum()

    val fats: Float
        get() = food.mapNotNull { it.fats }.sum()
}
