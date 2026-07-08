package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime

class MealPlanTest {
    private val mealIdentity = MealIdentity(Uuid.random())
    private val breakfast =
        Meal.Standard(
            identity = mealIdentity,
            timeWindow = Meal.TimeWindow.AllDay,
            mealType = MealType.Breakfast,
        )

    @Test
    fun add_standard_meal() {
        val timestamp = Instant.DISTANT_PAST
        val mealPlan = MealPlan()
        val events = mealPlan.add(breakfast, staticClock(timestamp))

        assertEquals(1, events.size)
        val event = events.first() as MealAddedEvent
        assertEquals(breakfast, event.meal)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun add_fails_when_existing_id() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        assertFailsWith<IllegalArgumentException> {
            mealPlan.add(breakfast)
        }
    }

    @Test
    fun add_fails_when_standard_meal_has_existing_type() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val otherIdentity = MealIdentity(Uuid.random())
        val anotherBreakfast = breakfast.copy(identity = otherIdentity)

        assertFailsWith<IllegalArgumentException> {
            mealPlan.add(anotherBreakfast)
        }
    }

    @Test
    fun add_custom_meal() {
        val customMeal =
            Meal.Custom(
                identity = MealIdentity(Uuid.random()),
                name = "Post Workout",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan()
        val events = mealPlan.add(customMeal)

        assertEquals(1, events.size)
        val event = events.first() as MealAddedEvent
        assertEquals(customMeal, event.meal)
    }

    @Test
    fun add_fails_when_custom_meal_has_existing_name() {
        val customMeal =
            Meal.Custom(
                identity = MealIdentity(Uuid.random()),
                name = "Post Workout",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan(meals = listOf(customMeal))
        val otherIdentity = MealIdentity(Uuid.random())
        val anotherPostWorkout = customMeal.copy(identity = otherIdentity)

        assertFailsWith<IllegalArgumentException> {
            mealPlan.add(anotherPostWorkout)
        }
    }

    @Test
    fun edit_custom_meal_name_and_time_window() {
        val customMeal =
            Meal.Custom(
                identity = mealIdentity,
                name = "Old Name",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan(meals = listOf(customMeal))
        val newTimeWindow = Meal.TimeWindow.Range(LocalTime(10, 0), LocalTime(11, 0))
        val events = mealPlan.edit(mealIdentity, "New Name", newTimeWindow)

        assertEquals(1, events.size)
        val event = events.first() as MealUpdatedEvent
        val updated = event.meal as Meal.Custom
        assertEquals("New Name", updated.name)
        assertEquals(newTimeWindow, updated.timeWindow)
    }

    @Test
    fun edit_does_not_emit_event_when_same_values() {
        val customMeal =
            Meal.Custom(
                identity = mealIdentity,
                name = "Name",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan(meals = listOf(customMeal))
        val events = mealPlan.edit(mealIdentity, "Name", Meal.TimeWindow.AllDay)

        assertTrue(events.isEmpty())
    }

    @Test
    fun edit_fails_when_renaming_custom_meal_to_existing_name() {
        val customMeal1 =
            Meal.Custom(
                identity = mealIdentity,
                name = "Meal 1",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val customMeal2 =
            Meal.Custom(
                identity = MealIdentity(Uuid.random()),
                name = "Meal 2",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan(meals = listOf(customMeal1, customMeal2))

        assertFailsWith<IllegalArgumentException> {
            mealPlan.edit(mealIdentity, "Meal 2", Meal.TimeWindow.AllDay)
        }
    }

    @Test
    fun edit_time_window_of_any_meal() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newTimeWindow = Meal.TimeWindow.Range(LocalTime(8, 0), LocalTime(9, 0))
        val events = mealPlan.edit(mealIdentity, newTimeWindow)

        assertEquals(1, events.size)
        val event = events.first() as MealUpdatedEvent
        assertEquals(newTimeWindow, event.meal.timeWindow)
    }

    @Test
    fun edit_fails_when_non_existent_meal() {
        val mealPlan = MealPlan()
        assertFailsWith<IllegalStateException> {
            mealPlan.edit(mealIdentity, Meal.TimeWindow.AllDay)
        }
    }

    @Test
    fun link_custom_meal_to_type() {
        val customMeal =
            Meal.Custom(
                identity = mealIdentity,
                name = "My Breakfast",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan(meals = listOf(customMeal))
        val events = mealPlan.link(mealIdentity, MealType.Breakfast)

        assertEquals(1, events.size)
        val event = events.first() as MealUpdatedEvent
        val updated = event.meal as Meal.Standard
        assertEquals(MealType.Breakfast, updated.mealType)
        assertEquals(mealIdentity, updated.identity)
    }

    @Test
    fun link_fails_when_existing_type() {
        val customMeal =
            Meal.Custom(
                identity = MealIdentity(Uuid.random()),
                name = "Custom",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan(meals = listOf(breakfast, customMeal))

        assertFailsWith<IllegalArgumentException> {
            mealPlan.link(customMeal.identity, MealType.Breakfast)
        }
    }

    @Test
    fun remove_meal() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val events = mealPlan.remove(mealIdentity, DeleteStrategy.Delete)

        assertEquals(1, events.size)
        val event = events.first() as MealRemovedEvent
        assertEquals(mealIdentity, event.identity)
        assertEquals(DeleteStrategy.Delete, event.strategy)
    }

    @Test
    fun remove_fails_when_non_existent_meal() {
        val mealPlan = MealPlan()
        assertFailsWith<IllegalStateException> {
            mealPlan.remove(mealIdentity, DeleteStrategy.Delete)
        }
    }

    @Test
    fun reorder_meals() {
        val meal2 =
            Meal.Custom(
                identity = MealIdentity(Uuid.random()),
                name = "Lunch",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan(meals = listOf(breakfast, meal2))
        val events = mealPlan.reorder(fromIndex = 0, toIndex = 1)

        assertEquals(1, events.size)
        val event = events.first() as MealPlanReorderedEvent
        assertEquals(listOf(meal2.identity, breakfast.identity), event.identities)
    }

    @Test
    fun reorder_returns_empty_events_when_same_position() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val events = mealPlan.reorder(0, 0)
        assertTrue(events.isEmpty())
    }

    @Test
    fun reorder_fails_when_invalid_indices() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        assertFailsWith<IllegalArgumentException> {
            mealPlan.reorder(0, 5)
        }
    }

    @Test
    fun apply_added_event() {
        val event = MealAddedEvent(breakfast, Clock.System.now())
        val mealPlan = MealPlan().apply(event)

        assertEquals(listOf(breakfast), mealPlan.meals)
    }

    @Test
    fun apply_updated_event() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val updatedBreakfast =
            breakfast.copy(timeWindow = Meal.TimeWindow.Range(LocalTime(7, 0), LocalTime(8, 0)))
        val event = MealUpdatedEvent(updatedBreakfast, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)
        assertEquals(updatedBreakfast, updatedPlan.meals.first())
    }

    @Test
    fun apply_removed_event() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val event = MealRemovedEvent(mealIdentity, DeleteStrategy.Delete, Clock.System.now())

        val updatedPlan = mealPlan.apply(event)
        assertTrue(updatedPlan.meals.isEmpty())
    }

    @Test
    fun apply_reordered_event() {
        val meal2 =
            Meal.Custom(
                identity = MealIdentity(Uuid.random()),
                name = "Lunch",
                timeWindow = Meal.TimeWindow.AllDay,
            )
        val mealPlan = MealPlan(meals = listOf(breakfast, meal2))
        val event =
            MealPlanReorderedEvent(listOf(meal2.identity, breakfast.identity), Clock.System.now())

        val updatedPlan = mealPlan.apply(event)
        assertEquals(listOf(meal2, breakfast), updatedPlan.meals)
    }

    @Test
    fun build_meal_plan_from_events() {
        val meal2 =
            Meal.Custom(
                identity = MealIdentity(Uuid.random()),
                name = "Lunch",
                timeWindow = Meal.TimeWindow.AllDay,
            )
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
}
