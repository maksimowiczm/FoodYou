package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.time.Clock
import kotlin.time.Duration
import kotlinx.datetime.LocalTime

data class MealPlan(val meals: List<Meal> = emptyList())

fun MealPlan.update(
    updatedMeals: List<Meal>,
    clock: Clock = Clock.System,
): List<MealPlanEvent> {
    require(updatedMeals.map { it.identity }.distinct().size == updatedMeals.size) {
        "Meal identities must be unique"
    }
    require(updatedMeals.isNotEmpty()) { "Must provide at least one meal" }
    if (updatedMeals == meals) return emptyList()

    val now = clock.now()
    val currentById = meals.associateBy { it.identity }
    val updatedById = updatedMeals.associateBy { it.identity }

    val added = updatedMeals.filter { it.identity !in currentById }
    val removed = meals.filter { it.identity !in updatedById }
    val changed = updatedMeals.filter { meal ->
        currentById[meal.identity]?.let { it != meal } == true
    }

    val removedIds = removed.map { it.identity }.toSet()
    val naturalOrder =
        meals.map { it.identity }.filterNot { it in removedIds } + added.map { it.identity }
    val targetOrder = updatedMeals.map { it.identity }
    val reordered = targetOrder != naturalOrder

    return buildList {
        added.forEach { add(MealAddedEvent(it, now)) }
        changed.forEach { add(MealUpdatedEvent(it, now)) }
        removed.forEach { add(MealDeletedEvent(it.identity, now)) }
        if (reordered) add(MealsReorderedEvent(targetOrder, now))
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

/**
 * Finds the most relevant meal for the given [time].
 *
 * It prioritizes:
 * 1. A meal whose [Meal.TimeWindow.Range] includes [time] OR is within [maxDistance].
 * 2. If multiple range meals are within [maxDistance], the closest one is chosen.
 * 3. If no range meal is within [maxDistance], a meal with [Meal.TimeWindow.AllDay] is chosen.
 * 4. As a last resort, the first meal in the plan is returned.
 *
 * @param time The time to check.
 * @param maxDistance The maximum allowed distance to a meal's time window.
 * @return The best matching [Meal].
 */
fun MealPlan.activeMeal(time: LocalTime, maxDistance: Duration): Meal =
    meals
        .filter { meal -> meal.timeWindow is Meal.TimeWindow.Range }
        .map { meal -> meal to (meal.timeWindow as Meal.TimeWindow.Range).distanceInSeconds(time) }
        .filter { (_, distance) -> distance <= maxDistance.inWholeSeconds }
        .minByOrNull { (_, distance) -> distance }
        ?.first ?: meals.firstOrNull { it.timeWindow is Meal.TimeWindow.AllDay } ?: meals.first()

fun MealPlan.apply(event: MealPlanEvent): MealPlan =
    when (event) {
        is MealPlanInitializedEvent -> copy(meals = event.meals)
        is MealAddedEvent -> copy(meals = meals + event.meal)
        is MealUpdatedEvent ->
            copy(meals = meals.map { if (it.identity == event.meal.identity) event.meal else it })
        is MealDeletedEvent -> copy(meals = meals.filterNot { it.identity == event.identity })
        is MealsReorderedEvent -> {
            val byId = meals.associateBy { it.identity }
            copy(meals = event.order.mapNotNull { byId[it] })
        }
    }

fun Iterable<MealPlanEvent>.toMealPlan(): MealPlan =
    fold(MealPlan()) { state, event -> state.apply(event) }
