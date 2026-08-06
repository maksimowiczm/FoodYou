package com.maksimowiczm.foodyou.device.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.device.domain.DeviceSettings
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class DeviceSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val deviceDisplayNameProvider: DeviceDisplayNameProvider,
    private val systemDetails: SystemDetails,
) : DeviceSettingsRepository {
    override fun observe(): Flow<DeviceSettings> {
        return combine(dataStore.data, systemDetails.languageTag) { preferences, languageTag ->
            preferences.toDevice(deviceDisplayNameProvider, languageTag)
        }
    }

    override suspend fun save(device: DeviceSettings) {
        dataStore.updateData { it.toMutablePreferences().applyDevice(device) }

        when (val language = device.language) {
            null -> systemDetails.setSystemLanguage()
            else -> systemDetails.setLanguage(language.tag)
        }
    }
}

private suspend fun Preferences.toDevice(
    deviceDisplayNameProvider: DeviceDisplayNameProvider,
    languageTag: String?,
): DeviceSettings {
    val deviceName = this[OtherKeys.deviceName] ?: deviceDisplayNameProvider.provide()
    val hideScreen = this[OtherKeys.hideScreen] ?: false
    return DeviceSettings(
        name = deviceName,
        themeSettings = toThemeSettings(),
        nutrientsColors = toNutrientsColors(),
        language =
            when (languageTag) {
                null -> null
                else -> Language.entries.firstOrNull { it.tag == languageTag }
            },
        hideScreen = hideScreen,
    )
}

private fun MutablePreferences.applyDevice(device: DeviceSettings): MutablePreferences = apply {
    this[OtherKeys.deviceName] = device.name
    this[OtherKeys.hideScreen] = device.hideScreen
    applyThemeSettings(device.themeSettings)
    applyNutrientsColors(device.nutrientsColors)
}

private object OtherKeys {
    val deviceName = stringPreferencesKey("device:name")
    val hideScreen = booleanPreferencesKey("device:hide_screen")
}
