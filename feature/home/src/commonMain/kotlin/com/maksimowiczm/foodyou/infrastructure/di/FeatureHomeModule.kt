package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.feature.home.goals.GoalsViewModel
import com.maksimowiczm.foodyou.feature.home.master.HomeViewModel
import com.maksimowiczm.foodyou.feature.home.meals.card.MealsCardsViewModel
import com.maksimowiczm.foodyou.feature.home.meals.settings.MealsCardsSettingsViewModel
import com.maksimowiczm.foodyou.feature.home.personalization.HomePersonalizationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureHomeModule = module {
    viewModel { HomeViewModel(settingsRepository = userPreferencesRepository()) }
    viewModel {
        MealsCardsViewModel(
            observeDiaryMealsUseCase = get(),
            foodEntryRepository = get(),
            manualEntryRepository = get(),
            mealsPreferencesRepository = userPreferencesRepository(),
        )
    }
    viewModel {
        MealsCardsSettingsViewModel(mealsPreferencesRepository = userPreferencesRepository())
    }
    viewModel {
        GoalsViewModel(
            settingsRepository = userPreferencesRepository(),
            observeDiaryMealsUseCase = get(),
            goalsRepository = get(),
        )
    }
    viewModel { HomePersonalizationViewModel(settingsRepository = userPreferencesRepository()) }
}
