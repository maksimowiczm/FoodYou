package com.maksimowiczm.foodyou.app.ui.onboarding

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingModule = module { viewModelOf(::OnboardingViewModel) }
