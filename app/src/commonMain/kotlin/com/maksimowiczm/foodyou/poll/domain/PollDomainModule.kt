package com.maksimowiczm.foodyou.poll.domain

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.poll.domain.usecase.ObserveActivePollUseCase
import org.koin.core.module.Module

internal fun Module.pollDomainModule() {
    factory {
        ObserveActivePollUseCase(
            settingsRepository = userPreferencesRepository(),
            pollPreferencesRepository = userPreferencesRepository(),
            pollRepository = get(),
            dateProvider = get(),
        )
    }
}
