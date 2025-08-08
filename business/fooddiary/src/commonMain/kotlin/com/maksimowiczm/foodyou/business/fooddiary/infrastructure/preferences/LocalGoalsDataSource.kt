package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences

import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import kotlinx.coroutines.flow.Flow

internal interface LocalGoalsDataSource {

    fun observeWeeklyGoals(): Flow<WeeklyGoals>

    suspend fun updateWeeklyGoals(weeklyGoals: WeeklyGoals)
}
