package com.maksimowiczm.foodyou.app.infrastructure.config

import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.common.domain.NetworkConfig
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.binds
import org.koin.dsl.module

val configModule = module {
    factoryOf(::FoodYouConfig).binds(arrayOf(AppConfig::class, NetworkConfig::class))
}
