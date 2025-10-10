package com.maksimowiczm.foodyou.app.infrastructure.config

import com.maksimowiczm.foodyou.app.domain.AppConfig
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val configModule = module { factoryOf(::FoodYouConfig).bind<AppConfig>() }
