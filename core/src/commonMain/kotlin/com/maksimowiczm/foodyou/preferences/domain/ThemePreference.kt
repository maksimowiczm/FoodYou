package com.maksimowiczm.foodyou.preferences.domain

data class ThemePreference(
    val randomizeOnLaunch: Boolean,
    val themeOption: ThemeOption,
    val theme: Theme,
    val nutrientsColors: NutrientsColors,
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

fun Theme.random(): Theme.Custom {
    return Theme.Custom(
        seedColor = randomColor(),
        style = possibleStyles.random(),
        contrast = possibleContrasts.random(),
        isAmoled = (this as? Theme.Custom)?.isAmoled ?: false,
    )
}

/**
 * Provides random color generation with configurable alpha (transparency) values. Colors are
 * returned as unsigned long integers with 8-bit alpha, red, green, and blue components packed into
 * the upper 32 bits (alpha most significant, then red, green, blue), with the lower 32 bits
 * reserved and set to zero.
 *
 * @param alpha The alpha component (0u-255u) to be used in the color. Defaults to 255u (fully
 *   opaque).
 * @return A random color represented as a ULong in ARGB format
 */
private fun randomColor(alpha: UByte = 255u): ULong {
    val rgb = (0x000000..0xFFFFFF).random()
    val argb = (alpha.toUInt() shl 24) or rgb.toUInt()
    return argb.toULong() shl 32
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
