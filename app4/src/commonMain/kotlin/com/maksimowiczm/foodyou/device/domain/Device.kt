package com.maksimowiczm.foodyou.device.domain

import com.maksimowiczm.foodyou.common.domain.Language

class Device(
    name: String,
    themeSettings: ThemeSettings,
    nutrientsColors: NutrientsColors,
    language: Language?,
    hideScreen: Boolean,
) {
    var name: String = name
        private set

    var themeSettings: ThemeSettings = themeSettings
        private set

    var nutrientsColors: NutrientsColors = nutrientsColors
        private set

    var language: Language? = language
        private set

    var hideScreen: Boolean = hideScreen
        private set

    fun randomizeTheme(colorProvider: ColorProvider) {
        val isAmoled =
            when (val theme = themeSettings.theme) {
                is Theme.Custom -> theme.isAmoled
                else -> false
            }

        val newTheme = ThemeSettings.random(colorProvider = colorProvider, isAmoled = isAmoled)

        themeSettings = themeSettings.copy(theme = newTheme)
    }

    fun updateLanguage(language: Language?) {
        this.language = language
    }

    fun updateHideScreen(hideScreen: Boolean) {
        this.hideScreen = hideScreen
    }

    fun updateTheme(theme: Theme) {
        themeSettings = themeSettings.copy(theme = theme, randomizeOnLaunch = false)
    }

    fun updateRandomizeOnLaunch(randomize: Boolean) {
        themeSettings = themeSettings.copy(randomizeOnLaunch = randomize)
    }

    fun updateThemeOption(themeOption: ThemeOption) {
        themeSettings = themeSettings.copy(themeOption = themeOption)
    }

    fun updateNutrientsColors(nutrientsColors: NutrientsColors) {
        this.nutrientsColors = nutrientsColors
    }

    fun resetNutrientsColors() {
        this.nutrientsColors = NutrientsColors(proteins = null, carbohydrates = null, fats = null)
    }
}
