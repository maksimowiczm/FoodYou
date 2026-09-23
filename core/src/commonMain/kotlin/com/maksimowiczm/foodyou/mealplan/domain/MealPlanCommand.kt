package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language

sealed interface MealPlanCommand {
    data class Initialize(
        val language: Language,
        val meals: List<Meal>,
    ) : MealPlanCommand

    data class UpdateMeals(val meals: List<Meal>) : MealPlanCommand
}
