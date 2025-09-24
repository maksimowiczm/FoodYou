package com.maksimowiczm.foodyou.app.ui.changelog

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

fun Module.changelog() {
    viewModel {
        ChangelogViewModel(
            changelogRepository = get(),
            settingsRepository = userPreferencesRepository(),
            savedStateHandle = get(),
        )
    }
}
