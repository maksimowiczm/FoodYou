package com.maksimowiczm.foodyou.analytics.di

import com.maksimowiczm.foodyou.analytics.application.AppLaunchCommandHandler
import com.maksimowiczm.foodyou.analytics.domain.AccountAnalyticsRepository
import com.maksimowiczm.foodyou.analytics.infrastructure.AccountAnalyticsRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    factoryOf(::AppLaunchCommandHandler)
    factoryOf(::AccountAnalyticsRepositoryImpl).bind<AccountAnalyticsRepository>()
}
