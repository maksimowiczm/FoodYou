package com.maksimowiczm.foodyou.app.ui.changelog

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiChangelogModule = module {
    viewModel {
        ChangelogViewModel(
            changelogRepository = get(),
            settingsRepository = userPreferencesRepository(),
            savedStateHandle = get(),
        )
    }
}
