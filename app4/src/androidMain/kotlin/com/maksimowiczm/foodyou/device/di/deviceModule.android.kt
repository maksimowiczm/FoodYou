package com.maksimowiczm.foodyou.device.di

import android.os.Build
import android.provider.Settings
import com.maksimowiczm.foodyou.device.domain.DeviceDisplayNameProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

internal actual fun Module.deviceDisplayNameProvider():
    KoinDefinition<out DeviceDisplayNameProvider> = factory {
    DeviceDisplayNameProvider {
        Settings.Global.getString(androidContext().contentResolver, Settings.Global.DEVICE_NAME)
            ?: Build.MODEL
    }
}
