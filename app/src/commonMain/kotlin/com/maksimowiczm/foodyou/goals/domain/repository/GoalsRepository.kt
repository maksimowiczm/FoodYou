package com.maksimowiczm.foodyou.goals.domain.repository

import com.maksimowiczm.foodyou.goals.domain.entity.DailyGoal
import com.maksimowiczm.foodyou.goals.domain.entity.WeeklyGoals
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GoalsRepository {
    suspend fun updateWeeklyGoals(weeklyGoals: WeeklyGoals)

    fun observeWeeklyGoals(): Flow<WeeklyGoals>

    fun observeDailyGoals(date: LocalDate): Flow<DailyGoal>
}
