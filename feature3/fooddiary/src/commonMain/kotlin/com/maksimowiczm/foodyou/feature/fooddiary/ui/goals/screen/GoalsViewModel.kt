package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.GoalsPreference
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

internal class GoalsViewModel(
    private val observeMealsUseCase: ObserveMealsUseCase,
    dataStore: DataStore<Preferences>
) : ViewModel() {

    private val goalsPreference = dataStore.userPreference<GoalsPreference>()

    fun observeMeals(date: LocalDate) = observeMealsUseCase(date)

    fun observeGoals(date: LocalDate) = goalsPreference.observe().map {
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
