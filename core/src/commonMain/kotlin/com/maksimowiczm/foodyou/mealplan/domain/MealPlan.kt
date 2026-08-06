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

    val now = clock.now()
    val currentById = meals.associateBy { it.identity }
    val updatedById = updatedMeals.associateBy { it.identity }

    val added = updatedMeals.filter { it.identity !in currentById }
    val removed = meals.filter { it.identity !in updatedById }
    val changed = updatedMeals.filter { meal ->
        currentById[meal.identity]?.let { it != meal } == true
    }

    return buildList {
        added.forEach { add(MealAddedEvent(it, now)) }
        changed.forEach { add(MealUpdatedEvent(it, now)) }
        removed.forEach { add(MealDeletedEvent(it.identity, now)) }
    }
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
        is MealAddedEvent -> copy(meals = meals + event.meal)
        is MealUpdatedEvent ->
            copy(meals = meals.map { if (it.identity == event.meal.identity) event.meal else it })
        is MealDeletedEvent -> copy(meals = meals.filterNot { it.identity == event.identity })
    }

fun Iterable<MealPlanEvent>.toMealPlan(): MealPlan =
    fold(MealPlan()) { state, event -> state.apply(event) }
