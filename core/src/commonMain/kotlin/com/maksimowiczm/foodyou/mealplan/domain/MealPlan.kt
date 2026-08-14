package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.time.Clock
import kotlinx.datetime.LocalTime

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

/**
 * Finds a meal that is strictly active at the given time [now].
 *
 * A meal is strictly active if the time is within its defined [Meal.TimeWindow.Range].
 *
 * @param now The time to check.
 * @return The active [Meal], or `null` if no meal's time window covers [now].
 */
fun MealPlan.activeMealStrict(now: LocalTime): Meal? = meals.find { meal ->
    (meal.timeWindow as? Meal.TimeWindow.Range)?.isInside(now) == true
}

/**
 * Finds the most relevant meal for the given [time].
 *
 * It prioritizes:
 * 1. A meal whose [Meal.TimeWindow.Range] includes [time].
 * 2. The meal whose range is closest to [time].
 * 3. A meal with [Meal.TimeWindow.AllDay].
 * 4. The first meal in the plan.
 *
 * @param time The time to check.
 * @return The best matching [Meal].
 */
fun MealPlan.activeMeal(time: LocalTime): Meal =
    meals
        .filter { meal -> meal.timeWindow is Meal.TimeWindow.Range }
        .minByOrNull { meal ->
            val timeWindow = meal.timeWindow as Meal.TimeWindow.Range
            timeWindow.distanceInSeconds(time)
        } ?: meals.firstOrNull { it.timeWindow is Meal.TimeWindow.AllDay } ?: meals.first()

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
