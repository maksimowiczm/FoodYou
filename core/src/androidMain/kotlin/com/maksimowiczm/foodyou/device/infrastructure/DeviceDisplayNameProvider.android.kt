package com.maksimowiczm.foodyou.device.infrastructure

import android.content.Context
import android.os.Build
import android.provider.Settings
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

internal actual class DeviceDisplayNameProvider(private val context: Context) {
    actual suspend fun provide(): String =
        Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
            ?: Build.MODEL
}

internal actual fun Module.deviceDisplayNameProvider():
    KoinDefinition<out DeviceDisplayNameProvider> = factory {
    DeviceDisplayNameProvider(androidContext())
}
