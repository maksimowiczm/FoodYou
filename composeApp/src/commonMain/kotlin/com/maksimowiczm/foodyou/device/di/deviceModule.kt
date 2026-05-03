package com.maksimowiczm.foodyou.device.di

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.device.application.RandomizeThemeOnAppLaunchHandler
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import com.maksimowiczm.foodyou.device.domain.RandomColorProvider
import com.maksimowiczm.foodyou.device.infrastructure.ComposeRandomColorProvider
import com.maksimowiczm.foodyou.device.infrastructure.DeviceSettingsRepositoryImpl
import com.maksimowiczm.foodyou.device.infrastructure.deviceDisplayNameProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val deviceModule = module {
    eventHandlerOf(::RandomizeThemeOnAppLaunchHandler)
    factoryOf(::DeviceSettingsRepositoryImpl).bind<DeviceSettingsRepository>()
    deviceDisplayNameProvider()
    factoryOf(::ComposeRandomColorProvider).bind<RandomColorProvider>()
}
