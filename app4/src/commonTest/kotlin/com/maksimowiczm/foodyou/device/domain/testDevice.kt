package com.maksimowiczm.foodyou.device.domain

import com.maksimowiczm.foodyou.common.domain.Language

fun testDevice(
    name: String = "Test Device",
    themeSettings: ThemeSettings =
        ThemeSettings(
            randomizeOnLaunch = false,
            themeOption = ThemeOption.System,
            theme = Theme.Default,
        ),
    nutrientsColors: NutrientsColors =
        NutrientsColors(proteins = null, carbohydrates = null, fats = null),
    language: Language? = null,
    hideScreen: Boolean = false,
): Device =
    Device(
        name = name,
        themeSettings = themeSettings,
        nutrientsColors = nutrientsColors,
        language = language,
        hideScreen = hideScreen,
    )
