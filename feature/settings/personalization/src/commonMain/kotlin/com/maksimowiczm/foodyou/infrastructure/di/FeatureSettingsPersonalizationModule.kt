package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.PersonalizationScreenViewModel
import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.PersonalizeNutritionFactsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureSettingsPersonalizationModule = module {
    viewModel {
        PersonalizationScreenViewModel(
            settingsRepository = get(named(Settings::class.qualifiedName!!))
        )
    }
    viewModel {
        PersonalizeNutritionFactsViewModel(
            settingsRepository = get(named(Settings::class.qualifiedName!!))
        )
    }
}
