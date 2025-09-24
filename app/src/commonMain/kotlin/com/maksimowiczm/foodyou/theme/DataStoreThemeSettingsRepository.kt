package com.maksimowiczm.foodyou.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.infrastructure.datastore.AbstractDataStoreUserPreferencesRepository

internal class DataStoreThemeSettingsRepository(dataStore: DataStore<Preferences>) :
    AbstractDataStoreUserPreferencesRepository<ThemeSettings>(dataStore) {
    override fun Preferences.toUserPreferences(): ThemeSettings {
        return ThemeSettings(
            themeOption =
                runCatching {
                        ThemeOption.entries[
                                this[ThemeSettingsPreferencesKeys.themeOption]
                                    ?: ThemeOption.System.ordinal]
                    }
                    .getOrElse { ThemeOption.System },
            theme = getTheme(),
        )
    }

    override fun MutablePreferences.applyUserPreferences(updated: ThemeSettings) {
        this[ThemeSettingsPreferencesKeys.themeOption] = updated.themeOption.ordinal
        setTheme(updated.theme)
    }
}

private fun Preferences.getTheme(): Theme {
    val isDefault = this[ThemeSettingsPreferencesKeys.themeDefault] ?: false
    if (isDefault) return Theme.Default

    val isDynamic = this[ThemeSettingsPreferencesKeys.themeDynamicColor] ?: false
    if (isDynamic) return Theme.Dynamic

    val keyColorString = this[ThemeSettingsPreferencesKeys.themeKeyColor]
    val seedColor = keyColorString?.toULongOrNull(16)

    val style =
        runCatching {
                ThemeStyle.entries[
                        this[ThemeSettingsPreferencesKeys.themeStyle]
                            ?: ThemeStyle.TonalSpot.ordinal]
            }
            .getOrElse { ThemeStyle.TonalSpot }

    val contrast =
        runCatching {
                ThemeContrast.entries[
                        this[ThemeSettingsPreferencesKeys.themeContrast]
                            ?: ThemeContrast.Default.ordinal]
            }
            .getOrElse { ThemeContrast.Default }

    val isAmoled = this[ThemeSettingsPreferencesKeys.themeAmoled] ?: false

    if (isDynamic) return Theme.Dynamic
    if (seedColor == null) return Theme.Default
    return Theme.Custom(
        seedColor = seedColor,
        style = style,
        contrast = contrast,
        isAmoled = isAmoled,
    )
}

private fun MutablePreferences.setTheme(theme: Theme): MutablePreferences = apply {
    when (theme) {
        is Theme.Default -> {
            this[ThemeSettingsPreferencesKeys.themeDefault] = true
        }

        is Theme.Dynamic -> {
            this[ThemeSettingsPreferencesKeys.themeDynamicColor] = true
        }

        is Theme.Custom -> {
            this[ThemeSettingsPreferencesKeys.themeDefault] = false
            this[ThemeSettingsPreferencesKeys.themeDynamicColor] = false
            this[ThemeSettingsPreferencesKeys.themeKeyColor] = theme.seedColor.toString(16)
            this[ThemeSettingsPreferencesKeys.themeStyle] = theme.style.ordinal
            this[ThemeSettingsPreferencesKeys.themeContrast] = theme.contrast.ordinal
            this[ThemeSettingsPreferencesKeys.themeAmoled] = theme.isAmoled
        }
    }
}

private object ThemeSettingsPreferencesKeys {
    val themeOption = intPreferencesKey("theme:option")
    val themeDefault = booleanPreferencesKey("theme:default")
    val themeDynamicColor = booleanPreferencesKey("theme:dynamicColor")
    val themeKeyColor = stringPreferencesKey("theme:keyColor")
    val themeStyle = intPreferencesKey("theme:style")
    val themeContrast = intPreferencesKey("theme:contrast")
    val themeAmoled = booleanPreferencesKey("theme:amoled")
}
