package com.maksimowiczm.foodyou.preferences.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.extension.set
import com.maksimowiczm.foodyou.preferences.domain.NutrientsColors
import com.maksimowiczm.foodyou.preferences.domain.Theme
import com.maksimowiczm.foodyou.preferences.domain.ThemeContrast
import com.maksimowiczm.foodyou.preferences.domain.ThemeOption
import com.maksimowiczm.foodyou.preferences.domain.ThemePreference
import com.maksimowiczm.foodyou.preferences.domain.ThemeStyle

class ThemePreferenceProvider(dataStore: DataStore<Preferences>) :
    DataStoreProvider<ThemePreference>(dataStore) {
    override val key = ThemePreference::class.qualifiedName!!

    override fun Preferences.map() =
        ThemePreference(
            randomizeOnLaunch = get(randomizeOnLaunch) ?: false,
            themeOption = ThemeOption.entries[this[themeOption] ?: ThemeOption.System.ordinal],
            theme = toTheme(),
            nutrientsColors =
                NutrientsColors(
                    proteins = this[proteinsColor]?.toULong(),
                    carbohydrates = this[carbohydratesColor]?.toULong(),
                    fats = this[fatsColor]?.toULong(),
                ),
        )

    private fun Preferences.toTheme(): Theme {
        val isDefault = this[themeDefault] ?: false
        if (isDefault) return Theme.Default

        val isDynamic = this[themeDynamicColor] ?: false
        if (isDynamic) return Theme.Dynamic

        val keyColorString = this[themeKeyColor]
        val seedColor = keyColorString?.toULongOrNull(16)

        val isAmoled = this[themeAmoled] ?: false

        if (seedColor == null) return Theme.Default
        return Theme.Custom(
            seedColor = seedColor,
            style = themeStyle,
            contrast = themeContrast,
            isAmoled = isAmoled,
        )
    }

    override fun MutablePreferences.apply(value: ThemePreference) {
        this[themeOption] = value.themeOption.ordinal

        when (val theme = value.theme) {
            is Theme.Default -> {
                this[themeDefault] = true
                this[themeDynamicColor] = false
            }

            is Theme.Dynamic -> {
                this[themeDynamicColor] = true
                this[themeDefault] = false
            }

            is Theme.Custom -> {
                this[themeDefault] = false
                this[themeDynamicColor] = false
                this[themeKeyColor] = theme.seedColor.toString(16)
                this[ThemePreferenceProvider.themeStyle] = theme.style.ordinal
                this[ThemePreferenceProvider.themeContrast] = theme.contrast.ordinal
                this[themeAmoled] = theme.isAmoled
            }
        }

        this[randomizeOnLaunch] = value.randomizeOnLaunch

        this[proteinsColor] = value.nutrientsColors.proteins?.toLong()
        this[carbohydratesColor] = value.nutrientsColors.carbohydrates?.toLong()
        this[fatsColor] = value.nutrientsColors.fats?.toLong()
    }

    private val Preferences.themeStyle: ThemeStyle
        get() = runCatching {
            ThemeStyle.entries[
                    this[ThemePreferenceProvider.themeStyle] ?: ThemeStyle.TonalSpot.ordinal]
        }
            .getOrElse { ThemeStyle.TonalSpot }

    private val Preferences.themeContrast: ThemeContrast
        get() = runCatching {
            ThemeContrast.entries[
                    this[ThemePreferenceProvider.themeContrast] ?: ThemeContrast.Default.ordinal]
        }
            .getOrElse { ThemeContrast.Default }

    private companion object {
        val randomizeOnLaunch = booleanPreferencesKey("device:theme:random")
        val themeOption = intPreferencesKey("device:theme:option")
        val themeDefault = booleanPreferencesKey("device:theme:default")
        val themeDynamicColor = booleanPreferencesKey("device:theme:dynamicColor")
        val themeKeyColor = stringPreferencesKey("device:theme:keyColor")
        val themeStyle = intPreferencesKey("device:theme:style")
        val themeContrast = intPreferencesKey("device:theme:contrast")
        val themeAmoled = booleanPreferencesKey("device:theme:amoled")
        val proteinsColor = longPreferencesKey("device:nutrientsColors:proteins")
        val carbohydratesColor = longPreferencesKey("device:nutrientsColors:carbohydrates")
        val fatsColor = longPreferencesKey("device:nutrientsColors:fats")
    }
}
