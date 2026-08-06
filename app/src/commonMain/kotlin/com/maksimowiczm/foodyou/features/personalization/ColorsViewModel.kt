package com.maksimowiczm.foodyou.features.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import com.maksimowiczm.foodyou.device.domain.RandomColorProvider
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeOption
import com.maksimowiczm.foodyou.device.domain.randomizeTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ColorsViewModel(
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val colorProvider: RandomColorProvider,
) : ViewModel() {
    private val _themeSettings = deviceSettingsRepository.observe().map { it.themeSettings }

    val themeSettings =
        _themeSettings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _themeSettings.first() },
        )

    private val _nutrientsColors = deviceSettingsRepository.observe().map { it.nutrientsColors }
    val nutrientsColors =
        _nutrientsColors.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _nutrientsColors.first() },
        )

    fun updateThemeOption(themeOption: ThemeOption) {
        viewModelScope.launch {
            deviceSettingsRepository.update { device ->
                device.copy(themeSettings = device.themeSettings.copy(themeOption = themeOption))
            }
        }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            deviceSettingsRepository.update { device ->
                device.copy(
                    themeSettings =
                        device.themeSettings.copy(theme = theme, randomizeOnLaunch = false)
                )
            }
        }
    }

    fun setRandomizeTheme(randomize: Boolean) {
        viewModelScope.launch {
            deviceSettingsRepository.update { settings ->
                val updatedSettings =
                    settings.copy(
                        themeSettings = settings.themeSettings.copy(randomizeOnLaunch = randomize)
                    )
                if (randomize) updatedSettings.randomizeTheme(colorProvider) else updatedSettings
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
            deviceSettingsRepository.update { device ->
                device.copy(
                    nutrientsColors =
                        device.nutrientsColors.copy(
                            proteins = proteinsColor ?: device.nutrientsColors.proteins,
                            carbohydrates =
                                carbohydratesColor ?: device.nutrientsColors.carbohydrates,
                            fats = fatsColor ?: device.nutrientsColors.fats,
                        )
                )
            }
        }
    }

    fun resetNutrientsColors() {
        viewModelScope.launch {
            deviceSettingsRepository.update { device ->
                device.copy(
                    nutrientsColors =
                        device.nutrientsColors.copy(
                            proteins = null,
                            carbohydrates = null,
                            fats = null,
                        )
                )
            }
        }
    }
}
