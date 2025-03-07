package com.maksimowiczm.foodyou.data

import com.maksimowiczm.foodyou.data.model.DailyGoals
import com.maksimowiczm.foodyou.data.model.DiaryDay
import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface DiaryRepository {
    fun observeDiaryDay(date: LocalDate): Flow<DiaryDay>

    fun observeDailyGoals(): Flow<DailyGoals>

    suspend fun setDailyGoals(goals: DailyGoals)

    fun observeMeals(): Flow<List<Meal>>

    suspend fun createMeal(name: String, from: LocalTime, to: LocalTime)

    suspend fun updateMeal(meal: Meal)

    suspend fun deleteMeal(meal: Meal)
}
