package com.maksimowiczm.foodyou.device

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.device.application.RandomizeThemeOnAppLaunchHandler
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import com.maksimowiczm.foodyou.device.domain.RandomColorProvider
import com.maksimowiczm.foodyou.device.infrastructure.ComposeRandomColorProvider
import com.maksimowiczm.foodyou.device.infrastructure.DeviceSettingsRepositoryImpl
import com.maksimowiczm.foodyou.device.infrastructure.deviceDisplayNameProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.device() {
    eventHandlerOf(::RandomizeThemeOnAppLaunchHandler)
    factoryOf(::DeviceSettingsRepositoryImpl).bind<DeviceSettingsRepository>()
    deviceDisplayNameProvider()
    factoryOf(::ComposeRandomColorProvider).bind<RandomColorProvider>()
}
