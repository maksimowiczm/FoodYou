package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.home.presentation.HomeViewModel
import com.maksimowiczm.foodyou.feature.home.presentation.goals.GoalsViewModel
import com.maksimowiczm.foodyou.feature.home.presentation.meals.card.MealsCardsViewModel
import com.maksimowiczm.foodyou.feature.home.presentation.meals.settings.MealsCardsSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureHomeModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::MealsCardsViewModel)
    viewModelOf(::MealsCardsSettingsViewModel)
    viewModelOf(::GoalsViewModel)
}
