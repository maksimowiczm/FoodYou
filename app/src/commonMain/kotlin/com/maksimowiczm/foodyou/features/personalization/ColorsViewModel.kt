package com.maksimowiczm.foodyou.features.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.preferences.domain.NutrientsColors
import com.maksimowiczm.foodyou.preferences.domain.Theme
import com.maksimowiczm.foodyou.preferences.domain.ThemeOption
import com.maksimowiczm.foodyou.preferences.domain.ThemePreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
import com.maksimowiczm.foodyou.preferences.domain.random
import com.maksimowiczm.foodyou.preferences.domain.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ColorsViewModel(private val preferencesRepository: UserPreferencesRepository) : ViewModel() {
    private val _themeSettings = preferencesRepository.observe<ThemePreference>()

    val themeSettings =
        _themeSettings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _themeSettings.first() },
        )

    fun updateThemeOption(themeOption: ThemeOption) {
        viewModelScope.launch {
            preferencesRepository.update<ThemePreference> {
                it.copy(themeOption = themeOption)
            }
        }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            preferencesRepository.update<ThemePreference> {
                it.copy(
                    randomizeOnLaunch = false,
                    theme = theme,
                )
            }
        }
    }

    fun setRandomizeTheme(randomize: Boolean) {
        viewModelScope.launch {
            preferencesRepository.update<ThemePreference> {
                it.copy(
                    randomizeOnLaunch = randomize,
                    theme = if (randomize) it.theme.random() else it.theme,
                )
            }
        }
    }

    /**
     * Updates the colors used for displaying nutrients.
     *
     * @param proteinsColor The new color for proteins, or null to keep the current color.
     * @param carbohydratesColor The new color for carbohydrates, or null to keep the current color.
     * @param fatsColor The new color for fats, or null to keep the current color
     */
    fun updateNutrientsColors(
        proteinsColor: ULong? = null,
        carbohydratesColor: ULong? = null,
        fatsColor: ULong? = null,
    ) {
        viewModelScope.launch {
            preferencesRepository.update<ThemePreference> {
                it.copy(
                    nutrientsColors =
                        it.nutrientsColors.copy(
                            proteins = proteinsColor ?: it.nutrientsColors.proteins,
                            carbohydrates = carbohydratesColor ?: it.nutrientsColors.carbohydrates,
                            fats = fatsColor ?: it.nutrientsColors.fats,
                        )
                )
            }
        }
    }

    fun resetNutrientsColors() {
        viewModelScope.launch {
            preferencesRepository.update<ThemePreference> {
                it.copy(
                    nutrientsColors =
                        NutrientsColors(
                            proteins = null,
                            carbohydrates = null,
                            fats = null,
                        )
                )
            }
        }
    }
}
