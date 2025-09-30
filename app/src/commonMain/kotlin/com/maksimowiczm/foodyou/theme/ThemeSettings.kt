package com.maksimowiczm.foodyou.theme

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferences

data class ThemeSettings(
    val randomizeOnLaunch: Boolean,
    val themeOption: ThemeOption,
    val theme: Theme,
) : UserPreferences

sealed interface Theme {

    data object Default : Theme

    data object Dynamic : Theme

    data class Custom(
        val seedColor: ULong,
        val style: ThemeStyle,
        val contrast: ThemeContrast,
        val isAmoled: Boolean,
    ) : Theme
}

enum class ThemeStyle {
    TonalSpot,
    Neutral,
    Vibrant,
    Expressive,
    Rainbow,
    FruitSalad,
    Monochrome,
    Fidelity,
    Content,
}

enum class ThemeContrast {
    Default,
    Medium,
    High,
    Reduced,
}

enum class ThemeOption {
    System,
    Light,
    Dark,
}
