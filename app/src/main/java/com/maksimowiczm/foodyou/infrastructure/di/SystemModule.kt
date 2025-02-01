package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.system.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.feature.system.data.AndroidTodayDateProvider
import com.maksimowiczm.foodyou.feature.system.data.SystemInfoRepository
import com.maksimowiczm.foodyou.feature.system.data.TodayDateProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val systemModule = module {
    factoryOf(::AndroidSystemInfoRepository).bind<SystemInfoRepository>()

    single {
        AndroidTodayDateProvider()
    }.bind<TodayDateProvider>()
}
