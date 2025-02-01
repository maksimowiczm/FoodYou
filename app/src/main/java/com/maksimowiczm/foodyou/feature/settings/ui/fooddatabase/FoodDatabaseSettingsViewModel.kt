package com.maksimowiczm.foodyou.feature.settings.ui.fooddatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.settings.data.SettingsRepository
import com.maksimowiczm.foodyou.feature.system.data.model.Country
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FoodDatabaseSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val openFoodFactsSettings = observeOpenFoodFactsSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(
            stopTimeoutMillis = 2000
        ),
        initialValue = runBlocking { observeOpenFoodFactsSettings().first() }
    )

    private fun observeOpenFoodFactsSettings() = combine(
        settingsRepository.observeOpenFoodFactsEnabled(),
        settingsRepository.observeOpenFoodFactsCountry()
    ) { enabled, country ->
        if (enabled && country != null) {
            OpenFoodFactsSettings.Enabled(
                country = country,
                availableCountries = settingsRepository.countries
            )
        } else {
            OpenFoodFactsSettings.Disabled
        }
    }

    fun onOpenFoodFactsToggle(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                settingsRepository.enableOpenFoodFacts()
            } else {
                settingsRepository.disableOpenFoodFacts()
            }
        }
    }

    fun onOpenFoodFactsCountrySelected(country: Country) {
        viewModelScope.launch {
            settingsRepository.setOpenFoodFactsCountry(country)
        }
    }
}

sealed interface OpenFoodFactsSettings {
    data object Disabled : OpenFoodFactsSettings
    data class Enabled(
        val country: Country,
        val availableCountries: List<Country>
    ) : OpenFoodFactsSettings
}
