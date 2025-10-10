package com.maksimowiczm.foodyou.device.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.device.domain.Device
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
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
    val deviceName = this[OtherKeys.deviceName] ?: defaultDeviceNameProvider.provide()
    return Device(
        name = deviceName,
        themeSettings = toThemeSettings(),
        privacySettings = toPrivacySettings(),
    )
}

private fun MutablePreferences.applyDevice(device: Device): MutablePreferences = apply {
    this[OtherKeys.deviceName] = device.name
    applyThemeSettings(device.themeSettings)
    applyPrivacySettings(device.privacySettings)
}

private object OtherKeys {
    val deviceName = stringPreferencesKey("device:name")
}
