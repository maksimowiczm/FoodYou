package com.maksimowiczm.foodyou.app.ui.personalization

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val personalizationModule = module {
    viewModelOf(::PersonalizationViewModel)
    viewModelOf(::PersonalizeNutritionFactsViewModel)
}
