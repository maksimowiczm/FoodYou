@file:MustUseReturnValues

package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.Decider
import kotlin.time.Duration
import kotlinx.datetime.LocalTime

data class MealPlan(val meals: List<Meal> = emptyList())

fun MealPlan.decide(command: MealPlanCommand): List<MealPlanEvent> =
    when (command) {
        is MealPlanCommand.Initialize -> {
            require(command.meals.isNotEmpty()) { "Cannot initialize meal plan: No meals provided" }
            check(this.meals.isEmpty()) { "Meal plan already initialized" }
            listOf(
                MealPlanInitializedEvent(
                    language = command.language,
                    meals = command.meals,
                    timestamp = command.timestamp,
                )
            )
        }

        is MealPlanCommand.UpdateMeals -> {
            val updatedMeals = command.meals
            val timestamp = command.timestamp
            require(updatedMeals.map { it.id }.distinct().size == updatedMeals.size) {
                "Meal identities must be unique"
            }
            require(updatedMeals.isNotEmpty()) { "Must provide at least one meal" }
            if (updatedMeals == meals) {
                emptyList()
            } else {
                val currentById = meals.associateBy { it.id }
                val updatedById = updatedMeals.associateBy { it.id }

                val added = updatedMeals.filter { it.id !in currentById }
                val removed = meals.filter { it.id !in updatedById }
                val changed = updatedMeals.filter { meal ->
                    currentById[meal.id]?.let { it != meal } == true
                }

                val removedIds = removed.map { it.id }.toSet()
                val naturalOrder =
                    meals.map { it.id }.filterNot { it in removedIds } + added.map { it.id }
                val targetOrder = updatedMeals.map { it.id }
                val reordered = targetOrder != naturalOrder

                buildList {
                    added.forEach { add(MealAddedEvent(it, timestamp)) }
                    changed.forEach { add(MealUpdatedEvent(it, timestamp)) }
                    removed.forEach { add(MealDeletedEvent(it.id, timestamp)) }
                    if (reordered) add(MealsReorderedEvent(targetOrder, timestamp))
                }
            }
        }
    }

fun MealPlan.apply(event: MealPlanEvent): MealPlan =
    when (event) {
        is MealPlanInitializedEvent -> copy(meals = event.meals)
        is MealAddedEvent -> copy(meals = meals + event.meal)
        is MealUpdatedEvent ->
            copy(meals = meals.map { if (it.id == event.meal.id) event.meal else it })

        is MealDeletedEvent -> copy(meals = meals.filterNot { it.id == event.mealId })
        is MealsReorderedEvent -> {
            val byId = meals.associateBy { it.id }
            copy(meals = event.order.mapNotNull { byId[it] })
        }
    }

fun Iterable<MealPlanEvent>.toMealPlan(): MealPlan =
    fold(MealPlan()) { state, event -> state.apply(event) }

val mealPlanDecider =
    Decider<MealPlanCommand, MealPlanEvent, MealPlan>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = MealPlan(),
    )

/**
 * Finds the most relevant meal for the given [time]. By default, it prioritizes meals with specific
 * time ranges that overlap with the time. If no time range matches, it looks for an "all-day" meal.
 * If neither is found, it falls back to the first meal in the plan.
 *
 * @param time The time of day to match.
 * @param maxDistance The maximum allowed distance from a meal's time window for it to be considered
 *   relevant.
 * @return The best matching [Meal].
 */
fun MealPlan.activeMeal(time: LocalTime, maxDistance: Duration): Meal =
    meals
        .filter { meal -> meal.timeWindow is Meal.TimeWindow.Range }
        .map { meal -> meal to (meal.timeWindow as Meal.TimeWindow.Range).distanceInSeconds(time) }
        .filter { (_, distance) -> distance <= maxDistance.inWholeSeconds }
        .minByOrNull { (_, distance) -> distance }
        ?.first ?: meals.firstOrNull { it.timeWindow is Meal.TimeWindow.AllDay } ?: meals.first()
