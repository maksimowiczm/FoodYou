package com.maksimowiczm.foodyou.mealplan.di

import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val mealPlanModule = module {
    factoryOf(::MealPlanService)
}
