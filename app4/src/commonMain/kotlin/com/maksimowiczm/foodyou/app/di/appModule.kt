package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.app.infrastructure.config.FoodYouConfig
import com.maksimowiczm.foodyou.common.domain.NetworkConfig
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.binds
import org.koin.dsl.module

val appModule = module {
    factoryOf(::FoodYouConfig).binds(arrayOf(AppConfig::class, NetworkConfig::class))
    factoryOf(::AppAccountManager)
}
