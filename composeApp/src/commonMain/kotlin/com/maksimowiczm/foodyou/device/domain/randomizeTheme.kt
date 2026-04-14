package com.maksimowiczm.foodyou.device.domain

fun DeviceSettings.randomizeTheme(colorProvider: RandomColorProvider): DeviceSettings {
    val currentTheme = themeSettings.theme
    val isAmoled = (currentTheme as? Theme.Custom)?.isAmoled ?: false

    val newTheme =
        Theme.Custom(
            seedColor = colorProvider.random(255),
            style = possibleStyles.random(),
            contrast = possibleContrasts.random(),
            isAmoled = isAmoled,
        )

    val newThemeSettings = themeSettings.copy(theme = newTheme)
    return copy(themeSettings = newThemeSettings)
}

private val possibleStyles =
    listOf(
        ThemeStyle.TonalSpot,
        ThemeStyle.Neutral,
        ThemeStyle.Vibrant,
        ThemeStyle.Expressive,
        ThemeStyle.Rainbow,
        ThemeStyle.FruitSalad,
        ThemeStyle.Fidelity,
        ThemeStyle.Content,
    )

private val possibleContrasts = listOf(ThemeContrast.Default)
