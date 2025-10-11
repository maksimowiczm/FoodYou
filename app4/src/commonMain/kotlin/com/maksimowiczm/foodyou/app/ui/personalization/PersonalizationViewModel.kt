package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalizationViewModel(
    private val deviceRepository: DeviceRepository,
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val _device = deviceRepository.observe()

    private val _energyFormat =
        accountManager
            .observePrimaryAccountId()
            .filterNotNull()
            .flatMapLatest { id -> accountRepository.observe(id) }
            .filterNotNull()
            .map { it.settings.energyFormat }

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
            val device = _device.first()
            device.updateHideScreen(secureScreen)
            deviceRepository.save(device)
        }
    }

    fun updateEnergyFormat(energyFormat: EnergyFormat) {
        viewModelScope.launch {
            val accountId =
                accountManager.observePrimaryAccountId().first() ?: error("No primary account")
            val account =
                accountRepository.observe(accountId).first()
                    ?: error("No account for id $accountId")
            account.updateSettings { it.copy(energyFormat = energyFormat) }
            accountRepository.save(account)
        }
    }
}
