package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import kotlin.time.Clock

data class MealPlan(val meals: List<Meal> = emptyList())

fun MealPlan.add(meal: Meal, clock: Clock = Clock.System): List<MealPlanEvent> {
    require(meals.none { it.identity == meal.identity }) {
        "Cannot add meal: ID ${meal.identity.id} already exists"
    }
    require(meals.none { it.name == meal.name }) {
        "Cannot add meal: Name '${meal.name}' already exists"
    }

    return listOf(MealAddedEvent(meal = meal, timestamp = clock.now()))
}

fun MealPlan.edit(
    identity: MealIdentity,
    name: String,
    timeWindow: Meal.TimeWindow,
    clock: Clock = Clock.System,
): List<MealPlanEvent> {
    val current = meals.find { it.identity == identity }
    checkNotNull(current) { "Meal with ID ${identity.id} not found" }

    require(meals.none { it.identity != identity && it.name == name }) {
        "Cannot rename meal: Name '$name' is already taken"
    }

    val updated =
        Meal(
            identity = identity,
            name = name,
            timeWindow = timeWindow,
        )

    if (updated == current) return emptyList()

    return listOf(MealUpdatedEvent(meal = updated, timestamp = clock.now()))
}

fun MealPlan.edit(
    identity: MealIdentity,
    timeWindow: Meal.TimeWindow,
    clock: Clock = Clock.System,
): List<MealPlanEvent> {
    val current = meals.find { it.identity == identity }
    checkNotNull(current) { "Meal with ID ${identity.id} not found" }

    val updated = current.copy(timeWindow = timeWindow)

    if (updated == current) return emptyList()

    return listOf(MealUpdatedEvent(meal = updated, timestamp = clock.now()))
}

fun MealPlan.remove(
    identity: MealIdentity,
    strategy: DeleteStrategy,
    clock: Clock = Clock.System,
): List<MealPlanEvent> {
    check(meals.any { it.identity == identity }) { "Meal with ID ${identity.id} not found" }
    return listOf(
        MealRemovedEvent(
            identity = identity,
            strategy = strategy,
            timestamp = clock.now(),
        )
    )
}

fun MealPlan.reorder(
    fromIndex: Int,
    toIndex: Int,
    clock: Clock = Clock.System,
): List<MealPlanEvent> {
    require(fromIndex in meals.indices && toIndex in meals.indices) { "Invalid indices" }
    if (fromIndex == toIndex) return emptyList()

    val newList = meals.toMutableList()
    val item = newList.removeAt(fromIndex)
    newList.add(toIndex, item)

    return listOf(
        MealPlanReorderedEvent(
            identities = newList.map { it.identity },
            timestamp = clock.now(),
        )
    )
}

fun MealPlan.apply(event: MealPlanEvent): MealPlan =
    when (event) {
        is MealAddedEvent -> copy(meals = meals + event.meal)
        is MealUpdatedEvent ->
            copy(meals = meals.map { if (it.identity == event.meal.identity) event.meal else it })
        is MealRemovedEvent -> copy(meals = meals.filter { it.identity != event.identity })
        is MealPlanReorderedEvent -> {
            val mealMap = meals.associateBy { it.identity }
            copy(meals = event.identities.mapNotNull { mealMap[it] })
        }
    }

fun Iterable<MealPlanEvent>.toMealPlan(): MealPlan =
    fold(MealPlan()) { state, event -> state.apply(event) }
