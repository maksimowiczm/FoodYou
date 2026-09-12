package com.maksimowiczm.foodyou.features.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalizationViewModel(
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val accountService: AccountService,
) : ViewModel() {
    private val _device = deviceSettingsRepository.observe()
    private val _account = accountService.observe().filterNotNull()

    val device =
        _device.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = runBlocking { _device.first() },
        )

    val account =
        _account.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = runBlocking { _account.first() },
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

    fun updateSingleProfileMode(enable: Boolean) {
        viewModelScope.launch {
            accountService.handle(
                AccountCommand.ChangeEnableSingleProfileMode(enable, Clock.System.now())
            )
        }
    }
}
