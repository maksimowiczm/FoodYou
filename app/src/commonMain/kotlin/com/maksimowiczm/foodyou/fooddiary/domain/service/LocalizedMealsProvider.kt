package com.maksimowiczm.foodyou.fooddiary.domain.service

import kotlinx.datetime.LocalTime

/**
 * Provides a list of localized meals. Implementations should return meals appropriate for the
 * user's locale.
 */
fun interface LocalizedMealsProvider {
    /**
     * Returns a list of meals, each represented as a Triple containing the meal name, start time,
     * and end time.
     *
     * @param languageTag ISO 3166-1 alpha-2 country code representing the desired locale
     */
    suspend fun getMeals(languageTag: String): List<LocalizedMeal>

    data class LocalizedMeal(val name: String, val start: LocalTime, val end: LocalTime)
}
