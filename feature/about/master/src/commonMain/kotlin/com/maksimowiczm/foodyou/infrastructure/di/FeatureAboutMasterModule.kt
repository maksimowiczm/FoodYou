package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.feature.about.master.presentation.ChangelogViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureAboutMasterModule = module {
    viewModelOf(::ChangelogViewModel)
    viewModel {
        ChangelogViewModel(
            changelogRepository = get(),
            settingsRepository = userPreferencesRepository(),
            savedStateHandle = get(),
        )
    }
}
