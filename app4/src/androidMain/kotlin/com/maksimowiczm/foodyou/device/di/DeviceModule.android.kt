package com.maksimowiczm.foodyou.device.di

import android.os.Build
import com.maksimowiczm.foodyou.device.infrastructure.DefaultDeviceNameProvider
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

internal actual fun Module.defaultDeviceNameProvider():
    KoinDefinition<out DefaultDeviceNameProvider> {
    return factory { DefaultDeviceNameProvider { Build.MODEL } }
}
