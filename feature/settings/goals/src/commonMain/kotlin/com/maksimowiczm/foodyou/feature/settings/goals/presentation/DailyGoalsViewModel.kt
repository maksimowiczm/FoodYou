package com.maksimowiczm.foodyou.feature.settings.goals.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateWeeklyGoalsCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveWeeklyGoalsQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DailyGoalsViewModel(queryBus: QueryBus, private val commandBus: CommandBus) :
    ViewModel() {

    val weeklyGoals =
        queryBus
            .dispatch<WeeklyGoals>(ObserveWeeklyGoalsQuery)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    private val _eventChannel = Channel<DailyGoalsViewModelEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun updateWeeklyGoals(weeklyGoals: WeeklyGoals) {
        viewModelScope.launch {
            commandBus.dispatch(UpdateWeeklyGoalsCommand(weeklyGoals))
            _eventChannel.send(DailyGoalsViewModelEvent.Updated)
        }
    }
}
