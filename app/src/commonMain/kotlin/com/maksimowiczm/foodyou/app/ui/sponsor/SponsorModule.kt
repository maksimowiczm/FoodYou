package com.maksimowiczm.foodyou.app.ui.sponsor

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

fun Module.sponsor() {
    viewModel {
        SponsorViewModel(
            sponsorRepository = get(),
            preferencesRepository = userPreferencesRepository(),
        )
    }
}
