package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.HomePersonalizationViewModel
import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.PersonalizationScreenViewModel
import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.PersonalizeNutritionFactsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureSettingsPersonalizationModule = module {
    viewModelOf(::PersonalizationScreenViewModel)
    viewModelOf(::PersonalizeNutritionFactsViewModel)
    viewModelOf(::HomePersonalizationViewModel)
}
