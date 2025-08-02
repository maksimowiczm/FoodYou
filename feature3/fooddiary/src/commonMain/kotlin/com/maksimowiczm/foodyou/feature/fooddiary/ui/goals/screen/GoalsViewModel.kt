package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.GoalsPreference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

internal class GoalsViewModel(
    date: LocalDate,
    private val observeMealsUseCase: ObserveMealsUseCase,
    dataStore: DataStore<Preferences>
) : ViewModel() {

    private val goalsPreference = dataStore.userPreference<GoalsPreference>()

    private val dateState = MutableStateFlow(date)
    val date = dateState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val meals = dateState.flatMapLatest { date ->
        observeMealsUseCase(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val goals = dateState.flatMapLatest { date ->
        goalsPreference.observe().map {
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )
}
