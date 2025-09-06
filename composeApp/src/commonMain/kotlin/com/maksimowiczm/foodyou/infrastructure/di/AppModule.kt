package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.FoodYouConfig
import com.maksimowiczm.foodyou.business.shared.domain.config.AppConfig
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.MealsProvider
import com.maksimowiczm.foodyou.infrastructure.ComposeMealsProvider
import com.maksimowiczm.foodyou.presentation.AppViewModel
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.log.Logger
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    factoryOf(::ComposeMealsProvider).bind<MealsProvider>()
    factoryOf(::FoodYouConfig).bind<AppConfig>()

    viewModelOf(::AppViewModel)

    factory { FoodYouLogger }.bind<Logger>()
}
