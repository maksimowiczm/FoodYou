package com.maksimowiczm.foodyou.app.infrastructure.shared.fooddiary

import kotlinx.datetime.LocalTime

/**
 * Provides a list of localized meals. Implementations should return meals appropriate for the
 * user's locale.
 */
fun interface LocalizedMealsProvider {
    /**
     * Returns a list of meals, each represented as a Triple containing the meal name, start time,
     * and end time.
     */
    suspend fun getMeals(): List<LocalizedMeal>

    data class LocalizedMeal(val name: String, val start: LocalTime, val end: LocalTime)
}
