package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.feature.settings.language.presentation.LanguageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureSettingsLanguageModule = module {
    viewModel {
        LanguageViewModel(
            translationRepository = get(),
            settingsRepository = userPreferencesRepository(),
        )
    }
}
