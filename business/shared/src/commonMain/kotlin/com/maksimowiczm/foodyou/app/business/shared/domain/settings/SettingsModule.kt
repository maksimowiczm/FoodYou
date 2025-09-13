package com.maksimowiczm.foodyou.app.business.shared.domain.settings

import com.maksimowiczm.foodyou.app.business.shared.di.eventHandler
import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.Module

fun Module.settingsModule() {
    eventHandler {
        AppLaunchEventHandler(
            settingsRepository = userPreferencesRepository(),
            changelogRepository = get(),
        )
    }
}
