package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealsUseCase.Meal
import kotlinx.datetime.LocalTime

interface MealRepository {
    suspend fun createMeal(name: String, from: LocalTime, to: LocalTime)

    suspend fun updateMeal(meal: Meal)

    suspend fun deleteMeal(meal: Meal)

    /**
     * Orders meals by their rank.
     *
     * @param map A map where the key is the meal ID and the value is the new rank.
     */
    suspend fun updateMealsRanks(map: Map<Long, Int>)
}
