package com.maksimowiczm.foodyou.device.di

import com.maksimowiczm.foodyou.common.event.di.eventHandler
import com.maksimowiczm.foodyou.device.application.AppLaunchedEventHandler
import org.koin.dsl.module

val deviceModule = module {
    eventHandler { AppLaunchedEventHandler(deviceRepository = get(), colorProvider = get()) }
}
