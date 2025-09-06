package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.feature.home.goals.GoalsViewModel
import com.maksimowiczm.foodyou.feature.home.master.HomeViewModel
import com.maksimowiczm.foodyou.feature.home.meals.card.MealsCardsViewModel
import com.maksimowiczm.foodyou.feature.home.meals.settings.MealsCardsSettingsViewModel
import com.maksimowiczm.foodyou.feature.home.personalization.HomePersonalizationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureHomeModule = module {
    viewModel { HomeViewModel(settingsRepository = get(named(Settings::class.qualifiedName!!))) }
    viewModel {
        MealsCardsViewModel(
            observeDiaryMealsUseCase = get(),
            foodEntryRepository = get(),
            manualEntryRepository = get(),
            mealsPreferencesRepository = get(named(MealsPreferences::class.qualifiedName!!)),
        )
    }
    viewModel {
        MealsCardsSettingsViewModel(
            mealsPreferencesRepository = get(named(MealsPreferences::class.qualifiedName!!))
        )
    }
    viewModel {
        GoalsViewModel(
            settingsRepository = get(named(Settings::class.qualifiedName!!)),
            observeDiaryMealsUseCase = get(),
            goalsRepository = get(),
        )
    }
    viewModel {
        HomePersonalizationViewModel(
            settingsRepository = get(named(Settings::class.qualifiedName!!))
        )
    }
}
