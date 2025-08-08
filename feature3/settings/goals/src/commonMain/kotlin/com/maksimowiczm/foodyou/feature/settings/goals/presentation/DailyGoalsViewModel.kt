package com.maksimowiczm.foodyou.feature.settings.goals.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateWeeklyGoalsCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveWeeklyGoalsQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.dispatchIgnoreResult
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class DailyGoalsViewModel(
    queryBus: QueryBus,
    private val commandBus: CommandBus,
    observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {

    private val _nutrientsOrder = observeSettingsUseCase.observe().map { it.nutrientsOrder }

    val nutrientsOrder =
        _nutrientsOrder.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _nutrientsOrder.first() },
        )

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
            commandBus.dispatchIgnoreResult(UpdateWeeklyGoalsCommand(weeklyGoals))
            _eventChannel.send(DailyGoalsViewModelEvent.Updated)
        }
    }
}
