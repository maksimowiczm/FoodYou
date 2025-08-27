package com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ExternalDatabasesViewModel(
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository
) : ViewModel() {

    val foodPreferences =
        foodSearchPreferencesRepository
            .observe()
            .map(::FoodPreferencesModel)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = FoodPreferencesModel(),
            )

    fun toggleOpenFoodFacts(newState: Boolean) {
        viewModelScope.launch {
            foodSearchPreferencesRepository.update {
                copy(openFoodFacts = openFoodFacts.copy(enabled = newState))
            }
        }
    }

    fun toggleUsda(newState: Boolean) {
        viewModelScope.launch {
            foodSearchPreferencesRepository.update { copy(usda = usda.copy(enabled = newState)) }
        }
    }
}
