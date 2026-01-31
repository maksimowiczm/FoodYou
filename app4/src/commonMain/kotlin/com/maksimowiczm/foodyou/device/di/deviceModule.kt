package com.maksimowiczm.foodyou.device.di

import com.maksimowiczm.foodyou.common.event.di.domainEventHandler
import com.maksimowiczm.foodyou.device.application.RandomizeThemeOnAppLaunchHandler
import com.maksimowiczm.foodyou.device.domain.DeviceDisplayNameProvider
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import com.maksimowiczm.foodyou.device.domain.RandomColorProvider
import com.maksimowiczm.foodyou.device.infrastructure.DeviceRepositoryImpl
import com.maksimowiczm.foodyou.device.infrastructure.composeRandomColorProvider
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val deviceModule = module {
    domainEventHandler {
        RandomizeThemeOnAppLaunchHandler(deviceRepository = get(), colorProvider = get())
    }
    factoryOf(::DeviceRepositoryImpl).bind<DeviceRepository>()
    deviceDisplayNameProvider()
    randomColorProvider()
}

internal expect fun Module.deviceDisplayNameProvider():
    KoinDefinition<out DeviceDisplayNameProvider>

internal fun Module.randomColorProvider(): KoinDefinition<out RandomColorProvider> {
    return factory { composeRandomColorProvider }.bind<RandomColorProvider>()
}
