package com.maksimowiczm.foodyou.device.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.device.domain.Device
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeContrast
import com.maksimowiczm.foodyou.device.domain.ThemeOption
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import com.maksimowiczm.foodyou.device.domain.ThemeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeviceRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val defaultDeviceNameProvider: DefaultDeviceNameProvider,
) : DeviceRepository {
    override fun observe(): Flow<Device> {
        return dataStore.data.map { it.toDevice(defaultDeviceNameProvider) }
    }

    override suspend fun save(device: Device) {
        dataStore.updateData { it.toMutablePreferences().applyDevice(device) }
    }
}

private suspend fun Preferences.toDevice(
    defaultDeviceNameProvider: DefaultDeviceNameProvider
): Device {
    val deviceName = this[Keys.deviceName] ?: defaultDeviceNameProvider.provide()

    val themeSettings =
        ThemeSettings(
            randomizeOnLaunch = this[Keys.randomizeOnLaunch] ?: false,
            themeOption = themeOption,
            theme = theme,
        )

    return Device(name = deviceName, themeSettings = themeSettings)
}

private fun MutablePreferences.applyDevice(device: Device): MutablePreferences {
    this[Keys.deviceName] = device.name
    this[Keys.randomizeOnLaunch] = device.themeSettings.randomizeOnLaunch
    this[Keys.themeOption] = device.themeSettings.themeOption.ordinal
    setTheme(device.themeSettings.theme)

    return this
}

private val Preferences.themeOption: ThemeOption
    get() =
        runCatching { ThemeOption.entries[this[Keys.themeOption] ?: ThemeOption.System.ordinal] }
            .getOrElse { ThemeOption.System }

private val Preferences.themeStyle: ThemeStyle
    get() =
        runCatching { ThemeStyle.entries[this[Keys.themeStyle] ?: ThemeStyle.TonalSpot.ordinal] }
            .getOrElse { ThemeStyle.TonalSpot }

private val Preferences.themeContrast: ThemeContrast
    get() =
        runCatching {
                ThemeContrast.entries[this[Keys.themeContrast] ?: ThemeContrast.Default.ordinal]
            }
            .getOrElse { ThemeContrast.Default }

private val Preferences.theme: Theme
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

private fun MutablePreferences.setTheme(theme: Theme): MutablePreferences = apply {
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

private object Keys {
    val deviceName = stringPreferencesKey("device:name")

    val randomizeOnLaunch = booleanPreferencesKey("device:theme:random")
    val themeOption = intPreferencesKey("device:theme:option")
    val themeDefault = booleanPreferencesKey("device:theme:default")
    val themeDynamicColor = booleanPreferencesKey("device:theme:dynamicColor")
    val themeKeyColor = stringPreferencesKey("device:theme:keyColor")
    val themeStyle = intPreferencesKey("device:theme:style")
    val themeContrast = intPreferencesKey("device:theme:contrast")
    val themeAmoled = booleanPreferencesKey("device:theme:amoled")
}
