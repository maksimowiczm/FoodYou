package com.maksimowiczm.foodyou.app.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.theme.NutrientsColors
import com.maksimowiczm.foodyou.theme.ThemeSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ThemeSettingsViewModel(
    private val themeRepository: UserPreferencesRepository<ThemeSettings>,
    private val nutrientsColorsRepository: UserPreferencesRepository<NutrientsColors>,
) : ViewModel() {
    val themeSettings =
        themeRepository
            .observe()
            .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null)

    val nutrientsColors =
        nutrientsColorsRepository
            .observe()
            .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null)

    fun updateThemeSettings(themeSettings: ThemeSettings) {
        viewModelScope.launch { themeRepository.update { themeSettings } }
    }

    fun updateNutrientsColors(nutrientsColors: NutrientsColors) {
        viewModelScope.launch { nutrientsColorsRepository.update { nutrientsColors } }
    }
}
