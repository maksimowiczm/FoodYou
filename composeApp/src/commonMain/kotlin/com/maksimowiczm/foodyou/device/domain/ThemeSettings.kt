package com.maksimowiczm.foodyou.device.domain

data class ThemeSettings(
    val randomizeOnLaunch: Boolean,
    val themeOption: ThemeOption,
    val theme: Theme,
) {
    companion object {
        private val possibleStyles =
            arrayOf(
                ThemeStyle.TonalSpot,
                ThemeStyle.Neutral,
                ThemeStyle.Vibrant,
                ThemeStyle.Expressive,
                ThemeStyle.Rainbow,
                ThemeStyle.FruitSalad,
                ThemeStyle.Fidelity,
                ThemeStyle.Content,
            )

        private val possibleContrast = arrayOf(ThemeContrast.Default)

        fun random(colorProvider: RandomColorProvider, isAmoled: Boolean): Theme.Custom =
            Theme.Custom(
                seedColor = colorProvider.random(255),
                style = possibleStyles.random(),
                contrast = possibleContrast.random(),
                isAmoled = isAmoled,
            )
    }
}

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
