package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.onboarding.ui.OnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingModule = module {
    viewModelOf(::OnboardingViewModel)
}
