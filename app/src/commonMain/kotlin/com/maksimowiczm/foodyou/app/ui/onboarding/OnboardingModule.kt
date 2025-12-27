package com.maksimowiczm.foodyou.app.ui.onboarding

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

fun Module.onboarding() {
    viewModel {
        OnboardingViewModel(
            foodSearchPreferencesRepository = userPreferencesRepository(),
            importTBCAUseCase = get(),
        )
    }
}
