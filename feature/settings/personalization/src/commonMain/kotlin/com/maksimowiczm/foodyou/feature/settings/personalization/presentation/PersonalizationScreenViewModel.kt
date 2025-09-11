package com.maksimowiczm.foodyou.feature.settings.personalization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.EnergyFormat
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.Settings
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class PersonalizationScreenViewModel(
    private val settingsRepository: UserPreferencesRepository<Settings>
) : ViewModel() {

    private val _secureScreen = settingsRepository.observe().map { it.secureScreen }
    val secureScreen =
        _secureScreen.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _secureScreen.first() },
        )

    fun toggleSecureScreen(newState: Boolean) {
        viewModelScope.launch { settingsRepository.update { copy(secureScreen = newState) } }
    }

    private val _energyUnit = settingsRepository.observe().map { it.energyFormat }
    val energyUnit =
        _energyUnit.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _energyUnit.first() },
        )

    fun setEnergyFormat(format: EnergyFormat) {
        viewModelScope.launch { settingsRepository.update { copy(energyFormat = format) } }
    }
}
