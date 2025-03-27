package com.maksimowiczm.foodyou.feature.garbage.data

import com.maksimowiczm.foodyou.feature.garbage.data.model.DailyGoals
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    fun observeDailyGoals(): Flow<DailyGoals>

    suspend fun setDailyGoals(goals: DailyGoals)
}
