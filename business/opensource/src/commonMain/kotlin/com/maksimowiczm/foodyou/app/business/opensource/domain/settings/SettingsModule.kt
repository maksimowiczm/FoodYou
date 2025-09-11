package com.maksimowiczm.foodyou.app.business.opensource.domain.settings

import com.maksimowiczm.foodyou.app.business.opensource.di.eventHandler
import com.maksimowiczm.foodyou.app.business.opensource.di.userPreferencesRepository
import org.koin.core.module.Module

fun Module.settingsModule() {
    eventHandler {
        AppLaunchEventHandler(
            settingsRepository = userPreferencesRepository(),
            observeChangelogUseCase = get(),
        )
    }
}
