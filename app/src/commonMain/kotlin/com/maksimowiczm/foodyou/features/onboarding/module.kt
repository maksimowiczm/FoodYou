package com.maksimowiczm.foodyou.features.onboarding

import com.maksimowiczm.foodyou.features.onboarding.dataimport.foodyou3.ImportFoodYou3DatabaseUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf

internal fun Module.onboarding() {
    viewModelOf(::OnboardingViewModel)
    factoryOf(::ImportFoodYou3DatabaseUseCase)
}
