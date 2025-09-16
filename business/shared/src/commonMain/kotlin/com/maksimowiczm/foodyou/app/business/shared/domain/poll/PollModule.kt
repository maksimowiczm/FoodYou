package com.maksimowiczm.foodyou.app.business.shared.domain.poll

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.Module

internal fun Module.pollModule() {
    factory {
        ObserveActivePollUseCase(
            settingsRepository = userPreferencesRepository(),
            pollPreferencesRepository = userPreferencesRepository(),
            pollRepository = get(),
            dateProvider = get(),
        )
    }
}
