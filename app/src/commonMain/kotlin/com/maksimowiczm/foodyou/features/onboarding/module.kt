package com.maksimowiczm.foodyou.features.onboarding

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.onboarding() {
    viewModelOf(::OnboardingViewModel)
}
