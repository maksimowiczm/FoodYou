package com.maksimowiczm.foodyou.feature.settings.personalization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommand
import com.maksimowiczm.foodyou.business.settings.domain.EnergyFormat
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class PersonalizationScreenViewModel(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val commandBus: CommandBus,
) : ViewModel() {

    private val settingsFlow = observeSettingsUseCase.observe()

    private val _secureScreen = settingsFlow.map { it.secureScreen }
    val secureScreen =
        _secureScreen.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _secureScreen.first() },
        )

    fun toggleSecureScreen(newState: Boolean) {
        viewModelScope.launch {
            commandBus.dispatch(PartialSettingsUpdateCommand(secureScreen = newState))
        }
    }

    private val _energyUnit = settingsFlow.map { it.energyFormat }
    val energyUnit =
        _energyUnit.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _energyUnit.first() },
        )

    fun setEnergyFormat(format: EnergyFormat) {
        viewModelScope.launch {
            commandBus.dispatch(PartialSettingsUpdateCommand(energyFormat = format))
        }
    }
}
