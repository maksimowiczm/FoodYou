package com.maksimowiczm.foodyou.app.ui.personalization

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

fun Module.personalization() {
    viewModel { PersonalizationScreenViewModel(settingsRepository = userPreferencesRepository()) }
    viewModel {
        PersonalizeNutritionFactsViewModel(settingsRepository = userPreferencesRepository())
    }
}
