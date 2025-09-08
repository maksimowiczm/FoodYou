package com.maksimowiczm.foodyou.fooddiary.domain.entity

import kotlinx.datetime.LocalTime

/**
 * Represents a meal in the food diary.
 *
 * @property id Unique identifier for the meal.
 * @property name Name of the meal (e.g., Breakfast, Lunch).
 * @property from Start time of the meal.
 * @property to End time of the meal.
 * @property rank Order of the meal in the day (e.g., 1 for Breakfast, 2 for Lunch, etc.).
 */
data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int,
)
