package com.maksimowiczm.foodyou.app.ui.personalization

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiPersonalizationModule = module {
    viewModel { PersonalizationScreenViewModel(settingsRepository = userPreferencesRepository()) }
    viewModel {
        PersonalizeNutritionFactsViewModel(settingsRepository = userPreferencesRepository())
    }
}
