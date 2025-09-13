package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.FoodYouConfig
import com.maksimowiczm.foodyou.app.business.opensource.domain.config.AppConfig
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.Settings
import com.maksimowiczm.foodyou.presentation.AppViewModel
import com.maksimowiczm.foodyou.shared.common.FoodYouLogger
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    factoryOf(::FoodYouConfig).bind<AppConfig>()

    viewModel { AppViewModel(settingsRepository = get(named(Settings::class.qualifiedName!!))) }

    factory { FoodYouLogger }.bind<Logger>()
}
