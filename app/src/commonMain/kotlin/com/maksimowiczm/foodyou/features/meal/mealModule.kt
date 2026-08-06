package com.maksimowiczm.foodyou.features.meal

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mealModule = module {
    viewModelOf(::MealScheduleViewModel)
}
