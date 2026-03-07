package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import com.maksimowiczm.foodyou.device.domain.RandomColorProvider
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeOption
import com.maksimowiczm.foodyou.device.domain.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ColorsViewModel(
    private val deviceRepository: DeviceRepository,
    private val colorProvider: RandomColorProvider,
) : ViewModel() {
    private val _themeSettings = deviceRepository.observe().map { it.themeSettings }

    val themeSettings =
        _themeSettings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(2_000),
            initialValue = runBlocking { _themeSettings.first() },
        )

    private val _nutrientsColors = deviceRepository.observe().map { it.nutrientsColors }
    val nutrientsColors =
        _nutrientsColors.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(2_000),
            initialValue = runBlocking { _nutrientsColors.first() },
        )

    fun updateThemeOption(themeOption: ThemeOption) {
        viewModelScope.launch {
            deviceRepository.update { device -> device.updateThemeOption(themeOption) }
        }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch { deviceRepository.update { device -> device.updateTheme(theme) } }
    }

    fun setRandomizeTheme(randomize: Boolean) {
        viewModelScope.launch {
            deviceRepository.update { device ->
                device.updateRandomizeOnLaunch(randomize)
                if (randomize) {
                    device.randomizeTheme(colorProvider)
                }
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
            deviceRepository.update { device ->
                device.updateNutrientsColors(
                    device.nutrientsColors.copy(
                        proteins = proteinsColor ?: device.nutrientsColors.proteins,
                        carbohydrates = carbohydratesColor ?: device.nutrientsColors.carbohydrates,
                        fats = fatsColor ?: device.nutrientsColors.fats,
                    )
                )
            }
        }
    }

    fun resetNutrientsColors() {
        viewModelScope.launch {
            deviceRepository.update { device -> device.resetNutrientsColors() }
        }
    }
}
