package com.maksimowiczm.foodyou.business.fooddiary.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalTime

interface MealRepository {
    fun observeMeal(mealId: Long): Flow<Meal?>

    fun observeMeals(): Flow<List<Meal>>

    suspend fun insertMealWithLastRank(name: String, from: LocalTime, to: LocalTime)

    suspend fun deleteMeal(mealId: Long)

    suspend fun updateMeal(id: Long, name: String, from: LocalTime, to: LocalTime)

    suspend fun reorderMeals(order: List<Long>)
}
