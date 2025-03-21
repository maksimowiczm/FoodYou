package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface DiaryRepository {
    fun observeDiaryDay(date: LocalDate): Flow<DiaryDay>

    fun observeDailyGoals(): Flow<DailyGoals>

    suspend fun setDailyGoals(goals: DailyGoals)

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
