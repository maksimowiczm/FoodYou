package com.maksimowiczm.foodyou.feature.mealredesign

import com.maksimowiczm.foodyou.feature.mealredesign.ui.MealsCardsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mealModule = module {
    viewModelOf(::MealsCardsViewModel)
}
