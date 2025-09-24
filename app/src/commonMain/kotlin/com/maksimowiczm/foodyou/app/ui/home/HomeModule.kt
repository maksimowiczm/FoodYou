package com.maksimowiczm.foodyou.app.ui.home

import com.maksimowiczm.foodyou.app.ui.home.goals.GoalsViewModel
import com.maksimowiczm.foodyou.app.ui.home.master.HomeViewModel
import com.maksimowiczm.foodyou.app.ui.home.meals.card.MealsCardsViewModel
import com.maksimowiczm.foodyou.app.ui.home.meals.settings.MealsCardsSettingsViewModel
import com.maksimowiczm.foodyou.app.ui.home.personalization.HomePersonalizationViewModel
import com.maksimowiczm.foodyou.app.ui.home.poll.PollsViewModel
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

fun Module.home() {
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

    viewModel {
        PollsViewModel(
            observeActivePollUseCase = get(),
            pollPreferencesRepository = userPreferencesRepository(),
        )
    }
}
