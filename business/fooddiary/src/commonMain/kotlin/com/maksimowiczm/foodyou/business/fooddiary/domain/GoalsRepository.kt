package com.maksimowiczm.foodyou.business.fooddiary.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GoalsRepository {
    suspend fun updateWeeklyGoals(weeklyGoals: WeeklyGoals)

    fun observeWeeklyGoals(): Flow<WeeklyGoals>

    fun observeDailyGoals(date: LocalDate): Flow<DailyGoal>
}
