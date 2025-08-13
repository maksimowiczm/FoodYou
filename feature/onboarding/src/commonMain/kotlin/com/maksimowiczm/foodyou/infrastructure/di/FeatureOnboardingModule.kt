package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.onboarding.presentation.OnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureOnboardingModule = module { viewModelOf(::OnboardingViewModel) }
