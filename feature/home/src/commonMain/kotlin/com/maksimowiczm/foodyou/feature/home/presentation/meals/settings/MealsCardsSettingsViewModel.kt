package com.maksimowiczm.foodyou.feature.home.presentation.meals.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class MealsCardsSettingsViewModel(private val mealRepository: MealRepository) :
    ViewModel() {

    private val _preferences = mealRepository.observeMealsPreferences()
    val preferences =
        _preferences.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _preferences.first() },
        )

    fun updatePreferences(preferences: MealsPreferences) {
        viewModelScope.launch { mealRepository.updateMealsPreferences { preferences } }
    }
}
