package com.maksimowiczm.foodyou.device.infrastructure

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeContrast
import com.maksimowiczm.foodyou.device.domain.ThemeOption
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import com.maksimowiczm.foodyou.device.domain.ThemeStyle

internal fun MutablePreferences.applyThemeSettings(
    themeSettings: ThemeSettings
): MutablePreferences = apply {
    applyThemeOption(themeSettings.themeOption)
    applyTheme(themeSettings.theme)
    this[ThemeSettingsKeys.randomizeOnLaunch] = themeSettings.randomizeOnLaunch
}

internal fun Preferences.toThemeSettings(): ThemeSettings =
    ThemeSettings(
        randomizeOnLaunch = this[ThemeSettingsKeys.randomizeOnLaunch] ?: false,
        themeOption = toThemeOption(),
        theme = toTheme(),
    )

private fun MutablePreferences.applyThemeOption(themeOption: ThemeOption): MutablePreferences {
    this[ThemeSettingsKeys.themeOption] = themeOption.ordinal
    return this
}

private fun Preferences.toThemeOption(): ThemeOption {
    return runCatching {
            ThemeOption.entries[this[ThemeSettingsKeys.themeOption] ?: ThemeOption.System.ordinal]
        }
        .getOrElse { ThemeOption.System }
}

private fun MutablePreferences.applyTheme(theme: Theme): MutablePreferences = apply {
    when (theme) {
        is Theme.Default -> {
            this[ThemeSettingsKeys.themeDefault] = true
            this[ThemeSettingsKeys.themeDynamicColor] = false
        }

        is Theme.Dynamic -> {
            this[ThemeSettingsKeys.themeDynamicColor] = true
            this[ThemeSettingsKeys.themeDefault] = false
        }

        is Theme.Custom -> {
            this[ThemeSettingsKeys.themeDefault] = false
            this[ThemeSettingsKeys.themeDynamicColor] = false
            this[ThemeSettingsKeys.themeKeyColor] = theme.seedColor.toString(16)
            this[ThemeSettingsKeys.themeStyle] = theme.style.ordinal
            this[ThemeSettingsKeys.themeContrast] = theme.contrast.ordinal
            this[ThemeSettingsKeys.themeAmoled] = theme.isAmoled
        }
    }
}

private fun Preferences.toTheme(): Theme {
    val isDefault = this[ThemeSettingsKeys.themeDefault] ?: false
    if (isDefault) return Theme.Default

    val isDynamic = this[ThemeSettingsKeys.themeDynamicColor] ?: false
    if (isDynamic) return Theme.Dynamic

    val keyColorString = this[ThemeSettingsKeys.themeKeyColor]
    val seedColor = keyColorString?.toULongOrNull(16)

    val isAmoled = this[ThemeSettingsKeys.themeAmoled] ?: false

    if (seedColor == null) return Theme.Default
    return Theme.Custom(
        seedColor = seedColor,
        style = themeStyle,
        contrast = themeContrast,
        isAmoled = isAmoled,
    )
}

private val Preferences.themeStyle: ThemeStyle
    get() =
        runCatching {
                ThemeStyle.entries[
                        this[ThemeSettingsKeys.themeStyle] ?: ThemeStyle.TonalSpot.ordinal]
            }
            .getOrElse { ThemeStyle.TonalSpot }

private val Preferences.themeContrast: ThemeContrast
    get() =
        runCatching {
                ThemeContrast.entries[
                        this[ThemeSettingsKeys.themeContrast] ?: ThemeContrast.Default.ordinal]
            }
            .getOrElse { ThemeContrast.Default }

private object ThemeSettingsKeys {
    val randomizeOnLaunch = booleanPreferencesKey("device:theme:random")
    val themeOption = intPreferencesKey("device:theme:option")
    val themeDefault = booleanPreferencesKey("device:theme:default")
    val themeDynamicColor = booleanPreferencesKey("device:theme:dynamicColor")
    val themeKeyColor = stringPreferencesKey("device:theme:keyColor")
    val themeStyle = intPreferencesKey("device:theme:style")
    val themeContrast = intPreferencesKey("device:theme:contrast")
    val themeAmoled = booleanPreferencesKey("device:theme:amoled")
}
