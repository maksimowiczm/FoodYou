package com.maksimowiczm.foodyou.features.personalization

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.personalization() {
    viewModelOf(::PersonalizationViewModel)
    viewModelOf(::PersonalizeNutritionFactsViewModel)
    viewModelOf(::ColorsViewModel)
}
