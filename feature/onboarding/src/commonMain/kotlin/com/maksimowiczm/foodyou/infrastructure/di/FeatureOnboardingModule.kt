package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.feature.onboarding.presentation.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureOnboardingModule = module {
    viewModel {
        OnboardingViewModel(
            importSwissUseCase = get(),
            foodSearchPreferencesRepository = userPreferencesRepository(),
        )
    }
}
