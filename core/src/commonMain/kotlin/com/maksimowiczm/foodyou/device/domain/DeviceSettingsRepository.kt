package com.maksimowiczm.foodyou.device.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface DeviceSettingsRepository {
    fun observe(): Flow<DeviceSettings>

    suspend fun save(device: DeviceSettings)

    suspend fun update(update: suspend (DeviceSettings) -> DeviceSettings) {
        val current = observe().first()
        val updated = update(current)
        save(updated)
    }
}
