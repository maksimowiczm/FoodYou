package com.maksimowiczm.foodyou.app.ui.sponsor

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiSponsorModule = module {
    viewModel {
        SponsorMessagesViewModel(
            sponsorRepository = get(),
            preferencesRepository = userPreferencesRepository(),
        )
    }
}
