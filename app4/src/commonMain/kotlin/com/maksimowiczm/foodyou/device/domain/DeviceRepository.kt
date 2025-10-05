package com.maksimowiczm.foodyou.device.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface DeviceRepository {
    fun observe(): Flow<Device>

    suspend fun load(): Device = observe().first()

    suspend fun save(device: Device)
}
