package com.maksimowiczm.foodyou.app.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.NutrientsColors
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.Settings
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.ThemeSettings
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.observeNutrientsColors
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.observeThemeSettings
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.updateNutrientsColors
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.updateThemeSettings
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ThemeSettingsViewModel(
    private val settingsRepository: UserPreferencesRepository<Settings>
) : ViewModel() {
    val themeSettings =
        settingsRepository
            .observeThemeSettings()
            .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null)

    val nutrientsColors =
        settingsRepository
            .observeNutrientsColors()
            .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null)

    fun updateThemeSettings(themeSettings: ThemeSettings) {
        viewModelScope.launch { settingsRepository.updateThemeSettings { themeSettings } }
    }

    fun updateNutrientsColors(nutrientsColors: NutrientsColors) {
        viewModelScope.launch { settingsRepository.updateNutrientsColors { nutrientsColors } }
    }
}
