package com.maksimowiczm.foodyou.app.ui.language

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiLanguageModule = module {
    viewModel {
        LanguageViewModel(
            translationRepository = get(),
            settingsRepository = userPreferencesRepository(),
        )
    }
}
