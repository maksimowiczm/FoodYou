package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.PersonalizationScreenViewModel
import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.PersonalizeNutritionFactsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureSettingsPersonalizationModule = module {
    viewModel { PersonalizationScreenViewModel(settingsRepository = userPreferencesRepository()) }
    viewModel {
        PersonalizeNutritionFactsViewModel(settingsRepository = userPreferencesRepository())
    }
}
