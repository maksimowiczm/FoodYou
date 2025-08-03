package com.maksimowiczm.foodyou.feature.fooddiary.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.GoalsPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

interface ObserveDailyGoalUseCase {
    fun observe(date: LocalDate): Flow<DailyGoal>
}

internal class ObserveDailyGoalUseCaseImpl(dataStore: DataStore<Preferences>) :
    ObserveDailyGoalUseCase {
    private val goalsPreference = dataStore.userPreference<GoalsPreference>()

    override fun observe(date: LocalDate): Flow<DailyGoal> = goalsPreference.observe().map {
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
