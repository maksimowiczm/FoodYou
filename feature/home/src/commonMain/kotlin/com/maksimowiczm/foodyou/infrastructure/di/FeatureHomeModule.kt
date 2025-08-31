package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.home.goals.GoalsViewModel
import com.maksimowiczm.foodyou.feature.home.master.HomeViewModel
import com.maksimowiczm.foodyou.feature.home.meals.card.MealsCardsViewModel
import com.maksimowiczm.foodyou.feature.home.meals.settings.MealsCardsSettingsViewModel
import com.maksimowiczm.foodyou.feature.home.personalization.HomePersonalizationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureHomeModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::MealsCardsViewModel)
    viewModelOf(::MealsCardsSettingsViewModel)
    viewModelOf(::GoalsViewModel)
    viewModelOf(::HomePersonalizationViewModel)
}
