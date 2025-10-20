package com.maksimowiczm.foodyou.app.ui.privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import com.maksimowiczm.foodyou.device.domain.update
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.food.search.domain.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PrivacyViewModel(
    private val deviceRepository: DeviceRepository,
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
) : ViewModel() {
    private val _privacySettings = deviceRepository.observe().map { it.privacySettings }

    val privacySettings =
        _privacySettings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _privacySettings.first() },
        )

    fun setPrivacySettings(foodYouServicesAllowed: Boolean? = null) {
        viewModelScope.launch {
            deviceRepository.update { device ->
                device.updatePrivacySettings {
                    it.copy(
                        foodYouServicesAllowed = foodYouServicesAllowed ?: it.foodYouServicesAllowed
                    )
                }
            }
        }
    }

    private val _foodSearchPreferences = foodSearchPreferencesRepository.observe()

    val foodSearchPreferences =
        _foodSearchPreferences.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _foodSearchPreferences.first() },
        )

    fun setFoodSearchPreferences(
        allowOpenFoodFacts: Boolean? = null,
        allowFoodDataCentralUSDA: Boolean? = null,
    ) {
        viewModelScope.launch {
            foodSearchPreferencesRepository.update { prefs ->
                prefs.copy(
                    allowOpenFoodFacts = allowOpenFoodFacts ?: prefs.allowOpenFoodFacts,
                    allowFoodDataCentralUSDA =
                        allowFoodDataCentralUSDA ?: prefs.allowFoodDataCentralUSDA,
                )
            }
        }
    }
}
