package com.maksimowiczm.foodyou.device.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.device.domain.Device
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DeviceRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val defaultDeviceNameProvider: DefaultDeviceNameProvider,
    private val systemDetails: SystemDetails,
) : DeviceRepository {
    override fun observe(): Flow<Device> {
        return combine(dataStore.data, systemDetails.languageTag) { preferences, languageTag ->
            preferences.toDevice(defaultDeviceNameProvider, languageTag)
        }
    }

    override suspend fun save(device: Device) {
        dataStore.updateData { it.toMutablePreferences().applyDevice(device) }

        when (val language = device.language) {
            null -> systemDetails.setSystemLanguage()
            else -> systemDetails.setLanguage(language.tag)
        }
    }
}

private suspend fun Preferences.toDevice(
    defaultDeviceNameProvider: DefaultDeviceNameProvider,
    languageTag: String?,
): Device {
    val deviceName = this[OtherKeys.deviceName] ?: defaultDeviceNameProvider.provide()
    val hideScreen = this[OtherKeys.hideScreen] ?: false
    return Device(
        name = deviceName,
        themeSettings = toThemeSettings(),
        privacySettings = toPrivacySettings(),
        language =
            when (languageTag) {
                null -> null
                else -> Language.entries.firstOrNull { it.tag == languageTag }
            },
        hideScreen = hideScreen,
    )
}

private fun MutablePreferences.applyDevice(device: Device): MutablePreferences = apply {
    this[OtherKeys.deviceName] = device.name
    this[OtherKeys.hideScreen] = device.hideScreen
    applyThemeSettings(device.themeSettings)
    applyPrivacySettings(device.privacySettings)
}

private object OtherKeys {
    val deviceName = stringPreferencesKey("device:name")
    val hideScreen = booleanPreferencesKey("device:hide_screen")
}
