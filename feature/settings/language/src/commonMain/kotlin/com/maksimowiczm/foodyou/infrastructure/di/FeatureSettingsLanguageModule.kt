package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.feature.settings.language.presentation.LanguageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureSettingsLanguageModule = module {
    viewModel {
        LanguageViewModel(
            translationRepository = get(),
            settingsRepository = get(named(Settings::class.qualifiedName!!)),
        )
    }
}
