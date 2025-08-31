package com.maksimowiczm.foodyou.feature.goals.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.domain.GoalsRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DailyGoalsViewModel(private val goalsRepository: GoalsRepository) : ViewModel() {

    val weeklyGoals =
        goalsRepository
            .observeWeeklyGoals()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    private val _eventChannel = Channel<DailyGoalsViewModelEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun updateWeeklyGoals(weeklyGoals: WeeklyGoals) {
        viewModelScope.launch {
            goalsRepository.updateWeeklyGoals(weeklyGoals)
            _eventChannel.send(DailyGoalsViewModelEvent.Updated)
        }
    }
}
