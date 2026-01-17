package com.maksimowiczm.foodyou.device.di

import com.maksimowiczm.foodyou.common.event.di.domainEventHandler
import com.maksimowiczm.foodyou.device.application.AppLaunchedEventHandler
import com.maksimowiczm.foodyou.device.domain.ColorProvider
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import com.maksimowiczm.foodyou.device.infrastructure.DefaultDeviceNameProvider
import com.maksimowiczm.foodyou.device.infrastructure.DeviceRepositoryImpl
import com.maksimowiczm.foodyou.device.infrastructure.composeColorProvider
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val deviceModule = module {
    domainEventHandler { AppLaunchedEventHandler(deviceRepository = get(), colorProvider = get()) }
    factoryOf(::DeviceRepositoryImpl).bind<DeviceRepository>()
    defaultDeviceNameProvider()
    colorProvider()
}

internal expect fun Module.defaultDeviceNameProvider():
    KoinDefinition<out DefaultDeviceNameProvider>

internal fun Module.colorProvider(): KoinDefinition<out ColorProvider> {
    return factory { composeColorProvider }.bind<ColorProvider>()
}
