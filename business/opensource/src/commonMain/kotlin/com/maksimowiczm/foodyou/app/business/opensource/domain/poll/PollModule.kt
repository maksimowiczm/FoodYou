package com.maksimowiczm.foodyou.app.business.opensource.domain.poll

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.Module

fun Module.pollModule() {
    factory {
        ObserveActivePollUseCase(
            settingsRepository = userPreferencesRepository(),
            pollPreferencesRepository = userPreferencesRepository(),
            pollRepository = get(),
            dateProvider = get(),
        )
    }
}
