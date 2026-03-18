package com.maksimowiczm.foodyou.analytics.di

import com.maksimowiczm.foodyou.analytics.application.AppLaunchUseCase
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsRepository
import com.maksimowiczm.foodyou.analytics.infrastructure.AnalyticsRepositoryImpl
import com.maksimowiczm.foodyou.common.event.di.domainEventBus
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    factoryOf(::AppLaunchUseCase)
    factory {
        AppLaunchUseCase(
            clock = get(),
            analyticsRepository = get(),
            appConfig = get(),
            eventBus = domainEventBus(),
        )
    }
    factoryOf(::AnalyticsRepositoryImpl).bind<AnalyticsRepository>()
}
