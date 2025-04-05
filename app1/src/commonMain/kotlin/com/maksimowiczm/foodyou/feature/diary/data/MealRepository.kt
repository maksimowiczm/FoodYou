package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalTime

interface MealRepository {
    fun observeMeals(): Flow<List<Meal>>

    fun observeMealById(id: Long): Flow<Meal?>

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
