package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime

class MealPlanTest {
    private val mealIdentity = MealIdentity(Uuid.random())
    private val breakfast =
        Meal(
            identity = mealIdentity,
            name = "Breakfast",
            timeWindow = Meal.TimeWindow.AllDay,
        )

    @Test
    fun `initialize empty plan produces MealPlanInitializedEvent`() {
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
    fun `initialize non-empty plan fails`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        assertFailsWith<IllegalStateException> {
            mealPlan.initialize(Language.English, listOf(breakfast))
        }
    }

    @Test
    fun `adding meal produces MealAddedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newMeal = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val newMeals = listOf(breakfast, newMeal)

        val events = mealPlan.update(newMeals, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealAddedEvent>(events.single())
        assertEquals(newMeal, event.meal)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun `updating meal produces MealUpdatedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val updatedBreakfast = breakfast.copy(name = "Updated Breakfast")
        val newMeals = listOf(updatedBreakfast)

        val events = mealPlan.update(newMeals, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealUpdatedEvent>(events.single())
        assertEquals(updatedBreakfast, event.meal)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun `removing meal produces MealDeletedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newMeals = emptyList<Meal>()

        val events = mealPlan.update(newMeals, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealDeletedEvent>(events.single())
        assertEquals(breakfast.identity, event.identity)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun `multiple changes produce multiple granular events`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val dinner = Meal(MealIdentity(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, dinner))

        val lunch = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val updatedBreakfast = breakfast.copy(name = "Big Breakfast")

        // breakfast updated, dinner removed, lunch added
        val newMeals = listOf(updatedBreakfast, lunch)

        val events = mealPlan.update(newMeals, staticClock(timestamp))

        assertEquals(3, events.size)
        assertIs<MealUpdatedEvent>(events.find { it is MealUpdatedEvent })
        assertIs<MealDeletedEvent>(events.find { it is MealDeletedEvent })
        assertIs<MealAddedEvent>(events.find { it is MealAddedEvent })
    }

    @Test
    fun `updating with duplicate identities fails`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val duplicateMeal = breakfast.copy(name = "Duplicate")

        assertFailsWith<IllegalArgumentException> {
            mealPlan.update(listOf(breakfast, duplicateMeal))
        }
    }

    @Test
    fun `updating with no changes returns no events`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val events = mealPlan.update(listOf(breakfast))
        assertEquals(0, events.size)
    }

    @Test
    fun `applying MealPlanInitializedEvent sets meals`() {
        val mealPlan = MealPlan()
        val defaultMeals = MealTemplates.forLanguage(Language.English)
        val event = MealPlanInitializedEvent(Language.English, defaultMeals, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(defaultMeals, updatedPlan.meals)
    }

    @Test
    fun `applying MealAddedEvent adds meal`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newMeal = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val event = MealAddedEvent(newMeal, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(listOf(breakfast, newMeal), updatedPlan.meals)
    }

    @Test
    fun `applying MealUpdatedEvent updates meal`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val updatedBreakfast = breakfast.copy(name = "Better Breakfast")
        val event = MealUpdatedEvent(updatedBreakfast, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(listOf(updatedBreakfast), updatedPlan.meals)
    }

    @Test
    fun `applying MealDeletedEvent removes meal`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val event = MealDeletedEvent(breakfast.identity, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(emptyList(), updatedPlan.meals)
    }

    @Test
    fun `aggregating event sequence produces correct state`() {
        val meal2 = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val updatedMeal2 = meal2.copy(name = "Better Lunch")
        val now = Clock.System.now()
        val events =
            listOf(
                MealPlanInitializedEvent(Language.English, listOf(breakfast), now),
                MealAddedEvent(meal2, now),
                MealUpdatedEvent(updatedMeal2, now),
                MealDeletedEvent(breakfast.identity, now),
            )

        val mealPlan = events.toMealPlan()

        assertEquals(listOf(updatedMeal2), mealPlan.meals)
    }

    @Test
    fun `activeMealStrict returns matching meal when time is within range`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val lunch = Meal(name = "Lunch", timeWindow = range("12:00", "14:00"))
        val plan = MealPlan(listOf(breakfast, lunch))

        assertEquals(breakfast, plan.activeMealStrict(LocalTime.parse("08:00")))
    }

    @Test
    fun `activeMealStrict returns null when no range matches time`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val lunch = Meal(name = "Lunch", timeWindow = range("12:00", "14:00"))
        val plan = MealPlan(listOf(breakfast, lunch))

        assertNull(plan.activeMealStrict(LocalTime.parse("10:00")))
    }

    @Test
    fun `activeMealStrict handles ranges wrapping past midnight`() {
        val dinner = Meal(name = "Dinner", timeWindow = range("22:00", "02:00"))
        val plan = MealPlan(listOf(dinner))

        assertEquals(dinner, plan.activeMealStrict(LocalTime.parse("23:00")))
        assertEquals(dinner, plan.activeMealStrict(LocalTime.parse("01:00")))
        assertNull(plan.activeMealStrict(LocalTime.parse("12:00")))
    }

    @Test
    fun `activeMeal returns active meal when time is within range`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val lunch = Meal(name = "Lunch", timeWindow = range("12:00", "14:00"))
        val plan = MealPlan(listOf(breakfast, lunch))

        assertEquals(breakfast, plan.activeMeal(LocalTime.parse("08:00")))
    }

    @Test
    fun `activeMeal returns closest meal when no range matches time`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val lunch = Meal(name = "Lunch", timeWindow = range("12:00", "14:00"))
        val plan = MealPlan(listOf(breakfast, lunch))

        // 10:00 is closer to breakfast (1h away) than to lunch (2h away)
        assertEquals(breakfast, plan.activeMeal(LocalTime.parse("10:00")))

        // 11:30 is closer to lunch (30m away) than to breakfast (2.5h away)
        assertEquals(lunch, plan.activeMeal(LocalTime.parse("11:30")))
    }

    @Test
    fun `activeMeal falls back to all day meal when no range meal is close`() {
        val allDay = Meal(name = "All Day", timeWindow = Meal.TimeWindow.AllDay)
        val plan = MealPlan(listOf(allDay))

        assertEquals(allDay, plan.activeMeal(LocalTime.parse("15:00")))
    }

    @Test
    fun `activeMeal falls back to first meal as last resort`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val plan = MealPlan(listOf(breakfast))

        assertEquals(breakfast, plan.activeMeal(LocalTime.parse("15:00")))
    }

    private fun range(start: String, end: String) =
        Meal.TimeWindow.Range(LocalTime.parse(start), LocalTime.parse(end))
}
