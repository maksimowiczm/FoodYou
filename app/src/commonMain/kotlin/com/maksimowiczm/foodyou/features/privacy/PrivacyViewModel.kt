package com.maksimowiczm.foodyou.features.privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PrivacyViewModel(
    private val openFoodFacts: OpenFoodFactsSettingsRepository,
    private val foodDataCentral: FoodDataCentralSettingsRepository,
) : ViewModel() {
    private val _foodSearchPreferences =
        combine(foodDataCentral.observe(), openFoodFacts.observe()) { usda, off ->
            PrivacyPreferences(
                allowOpenFoodFacts = off.remoteEnabled,
                allowFoodDataCentralUSDA = usda.remoteEnabled,
            )
        }

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
            val jobs =
                listOfNotNull(
                    allowOpenFoodFacts?.let {
                        async {
                            openFoodFacts.update { it.copy(remoteEnabled = allowOpenFoodFacts) }
                        }
                    },
                    allowFoodDataCentralUSDA?.let {
                        async {
                            foodDataCentral.update {
                                it.copy(remoteEnabled = allowFoodDataCentralUSDA)
                            }
                        }
                    },
                )

            awaitAll(*jobs.toTypedArray())
        }
    }
}
