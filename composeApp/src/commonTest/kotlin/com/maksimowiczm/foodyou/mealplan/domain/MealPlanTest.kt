package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
    fun add_meal() {
        val timestamp = Instant.fromEpochMilliseconds(1000)
        val mealPlan = MealPlan()
        val events = mealPlan.add(breakfast, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = events.first() as MealAddedEvent
        assertEquals(breakfast, event.meal)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun add_fails_when_meal_has_existing_identity() {
        val mealPlan = MealPlan(meals = listOf(breakfast))

        assertFailsWith<IllegalArgumentException> {
            mealPlan.add(breakfast)
        }
    }

    @Test
    fun add_fails_when_meal_has_existing_name() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val otherIdentity = MealIdentity(Uuid.random())
        val anotherBreakfast = breakfast.copy(identity = otherIdentity)

        assertFailsWith<IllegalArgumentException> {
            mealPlan.add(anotherBreakfast)
        }
    }

    @Test
    fun edit_meal_name_and_time_window() {
        val timestamp = Instant.fromEpochMilliseconds(2000)
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newName = "My Breakfast"
        val newTimeWindow = Meal.TimeWindow.Range(LocalTime(8, 0), LocalTime(9, 0))

        val events = mealPlan.edit(mealIdentity, newName, newTimeWindow, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = events.first() as MealUpdatedEvent
        assertEquals(newName, event.meal.name)
        assertEquals(newTimeWindow, event.meal.timeWindow)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun edit_fails_when_name_already_taken() {
        val meal2Identity = MealIdentity(Uuid.random())
        val meal2 = Meal(meal2Identity, "Lunch", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, meal2))

        assertFailsWith<IllegalArgumentException> {
            mealPlan.edit(meal2Identity, "Breakfast", Meal.TimeWindow.AllDay)
        }
    }

    @Test
    fun edit_only_time_window() {
        val timestamp = Instant.fromEpochMilliseconds(3000)
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newTimeWindow = Meal.TimeWindow.Range(LocalTime(7, 0), LocalTime(8, 0))

        val events = mealPlan.edit(mealIdentity, newTimeWindow, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = events.first() as MealUpdatedEvent
        assertEquals(breakfast.name, event.meal.name)
        assertEquals(newTimeWindow, event.meal.timeWindow)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun remove_meal() {
        val timestamp = Instant.fromEpochMilliseconds(4000)
        val mealPlan = MealPlan(meals = listOf(breakfast))

        val events = mealPlan.remove(mealIdentity, DeleteStrategy.Delete, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = events.first() as MealRemovedEvent
        assertEquals(mealIdentity, event.identity)
        assertEquals(DeleteStrategy.Delete, event.strategy)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun remove_fails_when_meal_not_found() {
        val mealPlan = MealPlan()
        assertFailsWith<IllegalStateException> {
            mealPlan.remove(mealIdentity, DeleteStrategy.Delete)
        }
    }

    @Test
    fun reorder_meals() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val meal2 = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, meal2))

        val events = mealPlan.reorder(0, 1, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = events.first() as MealPlanReorderedEvent
        assertEquals(listOf(meal2.identity, breakfast.identity), event.identities)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun reorder_no_change_returns_empty_list() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val events = mealPlan.reorder(0, 0)
        assertEquals(0, events.size)
    }

    @Test
    fun apply_add_event() {
        val mealPlan = MealPlan()
        val event = MealAddedEvent(breakfast, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(listOf(breakfast), updatedPlan.meals)
    }

    @Test
    fun apply_update_event() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val updatedBreakfast =
            breakfast.copy(timeWindow = Meal.TimeWindow.Range(LocalTime(7, 0), LocalTime(8, 0)))
        val event = MealUpdatedEvent(updatedBreakfast, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(updatedBreakfast, updatedPlan.meals.first())
    }

    @Test
    fun apply_remove_event() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val event = MealRemovedEvent(mealIdentity, DeleteStrategy.Delete, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(0, updatedPlan.meals.size)
    }

    @Test
    fun apply_reorder_event() {
        val meal2 = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, meal2))
        val event =
            MealPlanReorderedEvent(listOf(meal2.identity, breakfast.identity), Clock.System.now())

        val updatedPlan = mealPlan.apply(event)

        assertEquals(listOf(meal2, breakfast), updatedPlan.meals)
    }

    @Test
    fun toMealPlan_aggregates_events() {
        val meal2 = Meal(MealIdentity(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val events =
            listOf(
                MealAddedEvent(breakfast, Clock.System.now()),
                MealAddedEvent(meal2, Clock.System.now()),
                MealPlanReorderedEvent(
                    listOf(meal2.identity, breakfast.identity),
                    Clock.System.now(),
                ),
            )

        val mealPlan = events.toMealPlan()

        assertEquals(listOf(meal2, breakfast), mealPlan.meals)
    }

    private fun staticClock(instant: Instant) =
        object : Clock {
            override fun now(): Instant = instant
        }
}
