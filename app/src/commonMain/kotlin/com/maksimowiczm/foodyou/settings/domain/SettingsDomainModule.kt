package com.maksimowiczm.foodyou.settings.domain

import com.maksimowiczm.foodyou.common.infrastructure.koin.eventHandler
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.settings.domain.event.AppLaunchEventHandler
import org.koin.core.module.Module

internal fun Module.settingsModule() {
    eventHandler {
        AppLaunchEventHandler(
            settingsRepository = userPreferencesRepository(),
            changelogRepository = get(),
        )
    }
}
