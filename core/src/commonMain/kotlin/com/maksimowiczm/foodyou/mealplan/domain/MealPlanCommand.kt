package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.time.Instant

sealed interface MealPlanCommand {
    data class Initialize(
        val language: Language,
        val meals: List<Meal>,
        val timestamp: Instant,
    ) : MealPlanCommand

    data class UpdateMeals(
        val meals: List<Meal>,
        val timestamp: Instant,
    ) : MealPlanCommand
}
