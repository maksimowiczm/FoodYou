package com.maksimowiczm.foodyou.business.fooddiary.infrastructure

import com.maksimowiczm.foodyou.business.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.business.fooddiary.domain.GoalsRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreGoalsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

internal class GoalsRepositoryImpl(private val localGoals: DataStoreGoalsDataSource) :
    GoalsRepository {
    override suspend fun updateWeeklyGoals(weeklyGoals: WeeklyGoals) {
        localGoals.updateWeeklyGoals(weeklyGoals)
    }

    override fun observeWeeklyGoals(): Flow<WeeklyGoals> = localGoals.observeWeeklyGoals()

    override fun observeDailyGoals(date: LocalDate): Flow<DailyGoal> =
        observeWeeklyGoals().map {
            when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> it.monday
                DayOfWeek.TUESDAY -> it.tuesday
                DayOfWeek.WEDNESDAY -> it.wednesday
                DayOfWeek.THURSDAY -> it.thursday
                DayOfWeek.FRIDAY -> it.friday
                DayOfWeek.SATURDAY -> it.saturday
                DayOfWeek.SUNDAY -> it.sunday
            }
        }
}
