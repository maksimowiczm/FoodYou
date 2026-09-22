package com.maksimowiczm.foodyou.features.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.preferences.domain.EnergyUnitPreference
import com.maksimowiczm.foodyou.preferences.domain.FoodDiaryEntryTimestampsPreference
import com.maksimowiczm.foodyou.preferences.domain.HideScreenPreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
import com.maksimowiczm.foodyou.preferences.domain.update
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalizationViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val accountService: AccountService,
) : ViewModel() {
    val energyUnit =
        userPreferencesRepository
            .observe<EnergyUnitPreference>()
            .map { it.energyUnit }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = EnergyUnit.Kilocalories,
            )
    private val _singleProfileMode =
        accountService.observe().filterNotNull().map { it.singleProfileMode }
    val singleProfileMode =
        _singleProfileMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = runBlocking { _singleProfileMode.first() },
        )

    private val _singleProfileModeAllowed =
        accountService.observe().filterNotNull().map { it.profiles.size == 1 }
    val singleProfileModeAllowed =
        _singleProfileModeAllowed.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = runBlocking { _singleProfileModeAllowed.first() },
        )

    val hideScreen =
        userPreferencesRepository
            .observe<HideScreenPreference>()
            .map { it.hideScreen }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = false,
            )

    private val _foodDiaryEntryTimestamps =
        userPreferencesRepository.observe<FoodDiaryEntryTimestampsPreference>().map { it.enabled }

    val foodDiaryEntryTimestamps =
        _foodDiaryEntryTimestamps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = runBlocking { _foodDiaryEntryTimestamps.first() },
        )

    fun updateSecureScreen(secureScreen: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.update<HideScreenPreference> {
                HideScreenPreference(secureScreen)
            }
        }
    }

    fun updateEnergyUnit(energyUnit: EnergyUnit) {
        viewModelScope.launch {
            userPreferencesRepository.update<EnergyUnitPreference> {
                EnergyUnitPreference(energyUnit)
            }
        }
    }

    fun updateSingleProfileMode(enable: Boolean) {
        viewModelScope.launch {
            accountService.handle(AccountCommand.ChangeEnableSingleProfileMode(enable))
        }
    }

    fun updateFoodDiaryEntryTimestamps(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.update<FoodDiaryEntryTimestampsPreference> {
                FoodDiaryEntryTimestampsPreference(enabled)
            }
        }
    }
}
