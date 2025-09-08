package com.maksimowiczm.foodyou.business.settings.di

import com.maksimowiczm.foodyou.business.settings.domain.AppLaunchEventHandler
import com.maksimowiczm.foodyou.business.settings.domain.ObserveChangelogUseCase
import com.maksimowiczm.foodyou.business.shared.di.eventHandler
import com.maksimowiczm.foodyou.business.shared.di.userPreferencesRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val businessSettingsModule = module {
    factoryOf(::ObserveChangelogUseCase)
    eventHandler {
        AppLaunchEventHandler(
            settingsRepository = userPreferencesRepository(),
            observeChangelogUseCase = get(),
        )
    }
}
