package com.maksimowiczm.foodyou.analytics

import com.maksimowiczm.foodyou.analytics.application.AnalyticsService
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal fun Module.analytics() {
    factoryOf(::AnalyticsService)
}
