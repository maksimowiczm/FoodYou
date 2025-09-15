package com.maksimowiczm.foodyou.app.ui.onboarding.opensource

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiOnboardingModule = module {
    viewModel {
        OnboardingViewModel(
            importSwissUseCase = get(),
            foodSearchPreferencesRepository = userPreferencesRepository(),
        )
    }
}
