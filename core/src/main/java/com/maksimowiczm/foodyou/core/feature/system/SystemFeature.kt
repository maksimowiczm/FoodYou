package com.maksimowiczm.foodyou.core.feature.system

import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.system.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.core.feature.system.data.AndroidTodayDateProvider
import com.maksimowiczm.foodyou.core.feature.system.data.SystemInfoRepository
import com.maksimowiczm.foodyou.core.feature.system.data.TodayDateProvider
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val systemModule = module {
    factoryOf(::AndroidSystemInfoRepository).bind<SystemInfoRepository>()

    single { AndroidTodayDateProvider() }.bind<TodayDateProvider>()
}

/**
 * SystemFeature is a feature that provides system information.
 */
object SystemFeature : Feature.Koin {
    override fun KoinApplication.setup() {
        modules(systemModule)
    }
}
