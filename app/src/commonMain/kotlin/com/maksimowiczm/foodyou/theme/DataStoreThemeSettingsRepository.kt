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
            randomizeOnLaunch = this[Keys.randomizeOnLaunch] ?: false,
            themeOption = themeOption,
            theme = theme,
        )
    }

    override fun MutablePreferences.applyUserPreferences(updated: ThemeSettings) {
        this[Keys.randomizeOnLaunch] = updated.randomizeOnLaunch
        this[Keys.themeOption] = updated.themeOption.ordinal
        setTheme(updated.theme)
    }

    private companion object {
        val Preferences.themeOption: ThemeOption
            get() =
                runCatching {
                        ThemeOption.entries[this[Keys.themeOption] ?: ThemeOption.System.ordinal]
                    }
                    .getOrElse { ThemeOption.System }

        val Preferences.themeStyle: ThemeStyle
            get() =
                runCatching {
                        ThemeStyle.entries[this[Keys.themeStyle] ?: ThemeStyle.TonalSpot.ordinal]
                    }
                    .getOrElse { ThemeStyle.TonalSpot }

        val Preferences.themeContrast: ThemeContrast
            get() =
                runCatching {
                        ThemeContrast.entries[
                                this[Keys.themeContrast] ?: ThemeContrast.Default.ordinal]
                    }
                    .getOrElse { ThemeContrast.Default }

        val Preferences.theme: Theme
            get() {
                val isDefault = this[Keys.themeDefault] ?: false
                if (isDefault) return Theme.Default

                val isDynamic = this[Keys.themeDynamicColor] ?: false
                if (isDynamic) return Theme.Dynamic

                val keyColorString = this[Keys.themeKeyColor]
                val seedColor = keyColorString?.toULongOrNull(16)

                val isAmoled = this[Keys.themeAmoled] ?: false

                if (seedColor == null) return Theme.Default
                return Theme.Custom(
                    seedColor = seedColor,
                    style = themeStyle,
                    contrast = themeContrast,
                    isAmoled = isAmoled,
                )
            }

        fun MutablePreferences.setTheme(theme: Theme): MutablePreferences = apply {
            when (theme) {
                is Theme.Default -> {
                    this[Keys.themeDefault] = true
                    this[Keys.themeDynamicColor] = false
                }

                is Theme.Dynamic -> {
                    this[Keys.themeDynamicColor] = true
                    this[Keys.themeDefault] = false
                }

                is Theme.Custom -> {
                    this[Keys.themeDefault] = false
                    this[Keys.themeDynamicColor] = false
                    this[Keys.themeKeyColor] = theme.seedColor.toString(16)
                    this[Keys.themeStyle] = theme.style.ordinal
                    this[Keys.themeContrast] = theme.contrast.ordinal
                    this[Keys.themeAmoled] = theme.isAmoled
                }
            }
        }

        object Keys {
            val randomizeOnLaunch = booleanPreferencesKey("theme:random")

            val themeOption = intPreferencesKey("theme:option")

            val themeDefault = booleanPreferencesKey("theme:default")
            val themeDynamicColor = booleanPreferencesKey("theme:dynamicColor")
            val themeKeyColor = stringPreferencesKey("theme:keyColor")
            val themeStyle = intPreferencesKey("theme:style")
            val themeContrast = intPreferencesKey("theme:contrast")
            val themeAmoled = booleanPreferencesKey("theme:amoled")
        }
    }
}
