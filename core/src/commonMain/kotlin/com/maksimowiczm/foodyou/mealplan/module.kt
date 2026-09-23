package com.maksimowiczm.foodyou.mealplan

import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal fun Module.mealPlan() {
    factoryOf(::MealPlanService)
}
