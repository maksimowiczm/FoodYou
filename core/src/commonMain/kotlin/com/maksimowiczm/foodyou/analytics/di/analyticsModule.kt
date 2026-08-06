package com.maksimowiczm.foodyou.analytics.di

import com.maksimowiczm.foodyou.analytics.application.AnalyticsService
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val analyticsModule = module { factoryOf(::AnalyticsService) }
