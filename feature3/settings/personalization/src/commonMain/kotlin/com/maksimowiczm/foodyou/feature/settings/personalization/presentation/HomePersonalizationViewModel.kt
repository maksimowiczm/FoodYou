package com.maksimowiczm.foodyou.feature.settings.personalization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.application.command.SetHomeCardOrderCommand
import com.maksimowiczm.foodyou.business.settings.domain.HomeCard
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.dispatchIgnoreResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class HomePersonalizationViewModel(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val commandBus: CommandBus,
) : ViewModel() {

    private val _homeOrder = observeSettingsUseCase.observe().map { it.homeCardOrder }
    val homeOrder =
        _homeOrder.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _homeOrder.first() },
        )

    fun updateOrder(order: List<HomeCard>) {
        viewModelScope.launch { commandBus.dispatchIgnoreResult(SetHomeCardOrderCommand(order)) }
    }
}
