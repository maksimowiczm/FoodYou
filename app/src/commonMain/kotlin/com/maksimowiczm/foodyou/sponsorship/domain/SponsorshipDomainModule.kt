package com.maksimowiczm.foodyou.sponsorship.domain

import com.maksimowiczm.foodyou.common.infrastructure.koin.eventHandler
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.sponsorship.domain.event.AppLaunchEventHandler
import org.koin.core.module.Module

fun Module.sponsorshipDomainModule() {
    eventHandler {
        AppLaunchEventHandler(
            sponsorshipPreferencesRepository = userPreferencesRepository(),
            sponsorshipRepository = get(),
        )
    }
}
