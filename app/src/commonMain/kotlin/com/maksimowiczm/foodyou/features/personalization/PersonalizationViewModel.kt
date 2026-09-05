package com.maksimowiczm.foodyou.features.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import kotlin.time.Clock
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalizationViewModel(
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val accountService: AccountService,
) : ViewModel() {
    private val _device = deviceSettingsRepository.observe()

    private val _energyFormat = accountService.observe().filterNotNull().map { it.energyUnit }

    val energyFormat =
        _energyFormat.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _energyFormat.first() },
        )

    val secureScreen =
        _device
            .map { it.hideScreen }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = runBlocking { _device.map { it.hideScreen }.first() },
            )

    fun updateSecureScreen(secureScreen: Boolean) {
        viewModelScope.launch {
            deviceSettingsRepository.update { it.copy(hideScreen = secureScreen) }
        }
    }

    fun updateEnergyUnit(energyUnit: EnergyUnit) {
        viewModelScope.launch {
            accountService.handle(AccountCommand.ChangeEnergyUnit(energyUnit, Clock.System.now()))
        }
    }
}
