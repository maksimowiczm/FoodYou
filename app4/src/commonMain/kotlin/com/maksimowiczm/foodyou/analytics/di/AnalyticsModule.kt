package com.maksimowiczm.foodyou.analytics.di

import com.maksimowiczm.foodyou.device.application.AppLaunchedEventHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val analyticsModule = module { factoryOf(::AppLaunchedEventHandler) }
