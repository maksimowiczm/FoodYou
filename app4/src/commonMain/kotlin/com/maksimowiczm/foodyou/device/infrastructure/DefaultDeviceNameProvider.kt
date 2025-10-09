package com.maksimowiczm.foodyou.device.infrastructure

fun interface DefaultDeviceNameProvider {
    suspend fun provide(): String
}
