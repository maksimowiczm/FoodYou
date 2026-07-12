package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.time.Clock

data class MealPlan(val meals: List<Meal> = emptyList())

fun MealPlan.update(
    updatedMeals: List<Meal>,
    clock: Clock = Clock.System,
): List<MealPlanEvent> {
    require(updatedMeals.map { it.identity }.distinct().size == updatedMeals.size) {
        "Meal identities must be unique"
    }
    if (updatedMeals == meals) return emptyList()
    return listOf(MealPlanUpdatedEvent(updatedMeals, clock.now()))
}

fun MealPlan.initialize(
    language: Language,
    meals: List<Meal>,
    clock: Clock = Clock.System,
): List<MealPlanEvent> {
    require(meals.isNotEmpty()) { "Cannot initialize meal plan: No meals provided" }
    check(this.meals.isEmpty()) { "Meal plan already initialized" }
    return listOf(
        MealPlanInitializedEvent(
            language = language,
            meals = meals,
            timestamp = clock.now(),
        )
    )
}

fun MealPlan.apply(event: MealPlanEvent): MealPlan =
    when (event) {
        is MealPlanInitializedEvent -> copy(meals = event.meals)
        is MealPlanUpdatedEvent -> copy(meals = event.meals)
    }

fun Iterable<MealPlanEvent>.toMealPlan(): MealPlan =
    fold(MealPlan()) { state, event -> state.apply(event) }
