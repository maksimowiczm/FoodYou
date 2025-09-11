package com.maksimowiczm.foodyou.feature.home.meals.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.opensource.domain.fooddiary.MealsPreferences
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class MealsCardsSettingsViewModel(
    private val mealsPreferencesRepository: UserPreferencesRepository<MealsPreferences>
) : ViewModel() {

    private val _preferences = mealsPreferencesRepository.observe()
    val preferences =
        _preferences.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _preferences.first() },
        )

    fun updatePreferences(preferences: MealsPreferences) {
        viewModelScope.launch { mealsPreferencesRepository.update { preferences } }
    }
}
