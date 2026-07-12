package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class MealPlanTest {
    private val mealIdentity = MealIdentity(Uuid.random())
    private val breakfast =
        Meal(
            identity = mealIdentity,
            name = "Breakfast",
            timeWindow = Meal.TimeWindow.AllDay,
        )

    @Test
    fun initialize_meal_plan() {
        val timestamp = Instant.fromEpochMilliseconds(500)
        val templateMeals = MealTemplates.forLanguage(Language.English)
        val events = MealPlan().initialize(Language.English, templateMeals, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealPlanInitializedEvent>(events.single())
        assertEquals(Language.English, event.language)
        assertEquals(templateMeals, event.meals)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun initialize_fails_when_not_empty() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        assertFailsWith<IllegalStateException> {
            mealPlan.initialize(Language.English, listOf(breakfast))
        }
    }

    @Test
    fun update_meal_plan() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newMeal = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val newMeals = listOf(breakfast, newMeal)

        val events = mealPlan.update(newMeals, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealPlanUpdatedEvent>(events.single())
        assertEquals(newMeals, event.meals)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun update_fails_with_duplicate_identities() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val duplicateMeal = breakfast.copy(name = "Duplicate")

        assertFailsWith<IllegalArgumentException> {
            mealPlan.update(listOf(breakfast, duplicateMeal))
        }
    }

    @Test
    fun update_no_changes_returns_empty_list() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val events = mealPlan.update(listOf(breakfast))
        assertEquals(0, events.size)
    }

    @Test
    fun apply_initialize_event() {
        val mealPlan = MealPlan()
        val defaultMeals = MealTemplates.forLanguage(Language.English)
        val event = MealPlanInitializedEvent(Language.English, defaultMeals, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(defaultMeals, updatedPlan.meals)
    }

    @Test
    fun apply_update_event() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newMeal = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val newMeals = listOf(breakfast, newMeal)
        val event = MealPlanUpdatedEvent(newMeals, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(newMeals, updatedPlan.meals)
    }

    @Test
    fun toMealPlan_aggregates_events() {
        val meal2 = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val events =
            listOf(
                MealPlanInitializedEvent(Language.English, listOf(breakfast), Clock.System.now()),
                MealPlanUpdatedEvent(listOf(breakfast, meal2), Clock.System.now()),
            )

        val mealPlan = events.toMealPlan()

        assertEquals(listOf(breakfast, meal2), mealPlan.meals)
    }

    private fun staticClock(instant: Instant) =
        object : Clock {
            override fun now(): Instant = instant
        }
}
