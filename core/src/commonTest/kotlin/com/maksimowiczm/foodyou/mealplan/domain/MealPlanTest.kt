package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime

class MealPlanTest {
    private val mealId = MealId(Uuid.random())
    private val breakfast =
        Meal(
            id = mealId,
            name = "Breakfast",
            timeWindow = Meal.TimeWindow.AllDay,
        )

    @Test
    fun `initialize empty plan produces MealPlanInitializedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(500)
        val templateMeals = MealTemplates.forLanguage(Language.English)
        val events =
            MealPlan()
                .decide(MealPlanCommand.Initialize(Language.English, templateMeals, timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealPlanInitializedEvent>(events.single())
        assertEquals(Language.English, event.language)
        assertEquals(templateMeals, event.meals)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun `initialize non-empty plan fails`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val timestamp = Instant.fromEpochMilliseconds(500)
        assertFailsWith<IllegalStateException> {
            mealPlan.decide(
                MealPlanCommand.Initialize(Language.English, listOf(breakfast), timestamp)
            )
        }
    }

    @Test
    fun `adding meal produces MealAddedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newMeal = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val newMeals = listOf(breakfast, newMeal)

        val events = mealPlan.decide(MealPlanCommand.UpdateMeals(newMeals, timestamp))

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

        val events = mealPlan.decide(MealPlanCommand.UpdateMeals(newMeals, timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealUpdatedEvent>(events.single())
        assertEquals(updatedBreakfast, event.meal)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun `removing meal produces MealDeletedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val lunch = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, lunch))
        val newMeals = listOf(lunch)

        val events = mealPlan.decide(MealPlanCommand.UpdateMeals(newMeals, timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealDeletedEvent>(events.single())
        assertEquals(breakfast.id, event.mealId)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun `multiple changes produce multiple granular events`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, dinner))

        val lunch = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val updatedBreakfast = breakfast.copy(name = "Big Breakfast")

        // breakfast updated, dinner removed, lunch added
        val newMeals = listOf(updatedBreakfast, lunch)

        val events = mealPlan.decide(MealPlanCommand.UpdateMeals(newMeals, timestamp))

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
            mealPlan.decide(
                MealPlanCommand.UpdateMeals(
                    listOf(breakfast, duplicateMeal),
                    Instant.DISTANT_PAST,
                )
            )
        }
    }

    @Test
    fun `updating with no changes returns no events`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val events =
            mealPlan.decide(MealPlanCommand.UpdateMeals(listOf(breakfast), Instant.DISTANT_PAST))
        assertEquals(0, events.size)
    }

    @Test
    fun `reordering meals produces MealsReorderedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, dinner))

        val reordered = listOf(dinner, breakfast)

        val events = mealPlan.decide(MealPlanCommand.UpdateMeals(reordered, timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealsReorderedEvent>(events.single())
        assertEquals(listOf(dinner.id, breakfast.id), event.order)
        assertEquals(timestamp, event.timestamp)
    }

    @Test
    fun `reordering with simultaneous content change produces both events`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, dinner))

        val updatedBreakfast = breakfast.copy(name = "Big Breakfast")
        val newMeals = listOf(dinner, updatedBreakfast)

        val events = mealPlan.decide(MealPlanCommand.UpdateMeals(newMeals, timestamp))

        assertEquals(2, events.size)
        val updateEvent = assertIs<MealUpdatedEvent>(events.find { it is MealUpdatedEvent })
        assertEquals(updatedBreakfast, updateEvent.meal)

        val reorderEvent = assertIs<MealsReorderedEvent>(events.find { it is MealsReorderedEvent })
        assertEquals(listOf(dinner.id, breakfast.id), reorderEvent.order)
    }

    @Test
    fun `adding a meal in the middle produces MealAddedEvent and MealsReorderedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, dinner))

        val lunch = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        // lunch inserted between breakfast and dinner, not appended at the end
        val newMeals = listOf(breakfast, lunch, dinner)

        val events = mealPlan.decide(MealPlanCommand.UpdateMeals(newMeals, timestamp))

        assertEquals(2, events.size)
        val addedEvent = assertIs<MealAddedEvent>(events.find { it is MealAddedEvent })
        assertEquals(lunch, addedEvent.meal)

        val reorderEvent = assertIs<MealsReorderedEvent>(events.find { it is MealsReorderedEvent })
        assertEquals(
            listOf(breakfast.id, lunch.id, dinner.id),
            reorderEvent.order,
        )
    }

    @Test
    fun `adding a meal at the end does not produce MealsReorderedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val lunch = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)

        val events =
            mealPlan.decide(MealPlanCommand.UpdateMeals(listOf(breakfast, lunch), timestamp))

        assertEquals(1, events.size)
        assertIs<MealAddedEvent>(events.single())
    }

    @Test
    fun `deleting a meal does not spuriously produce MealsReorderedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, dinner))

        val events = mealPlan.decide(MealPlanCommand.UpdateMeals(listOf(dinner), timestamp))

        assertEquals(1, events.size)
        assertIs<MealDeletedEvent>(events.single())
    }

    @Test
    fun `deleting a meal from the middle does not spuriously produce MealsReorderedEvent`() {
        val timestamp = Instant.fromEpochMilliseconds(5000)
        val lunch = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, lunch, dinner))

        // remove lunch, keep breakfast and dinner in their original relative order
        val events =
            mealPlan.decide(MealPlanCommand.UpdateMeals(listOf(breakfast, dinner), timestamp))

        assertEquals(1, events.size)
        val event = assertIs<MealDeletedEvent>(events.single())
        assertEquals(lunch.id, event.mealId)
    }

    @Test
    fun `applying MealsReorderedEvent reorders meals`() {
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, dinner))
        val event =
            MealsReorderedEvent(listOf(dinner.id, breakfast.id), Instant.fromEpochSeconds(1))

        val updatedPlan = mealPlan.apply(event)

        assertEquals(listOf(dinner, breakfast), updatedPlan.meals)
    }

    @Test
    fun `aggregating update and reorder events produces correct state`() {
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val updatedBreakfast = breakfast.copy(name = "Big Breakfast")
        val now = Instant.fromEpochSeconds(1)
        val events =
            listOf(
                MealPlanInitializedEvent(Language.English, listOf(breakfast, dinner), now),
                MealUpdatedEvent(updatedBreakfast, now),
                MealsReorderedEvent(listOf(dinner.id, breakfast.id), now),
            )

        val mealPlan = events.toMealPlan()

        assertEquals(listOf(dinner, updatedBreakfast), mealPlan.meals)
    }

    @Test
    fun `updating with no order change and no content change returns no events`() {
        val dinner = Meal(MealId(Uuid.random()), "Dinner", Meal.TimeWindow.AllDay)
        val mealPlan = MealPlan(meals = listOf(breakfast, dinner))

        val events =
            mealPlan.decide(
                MealPlanCommand.UpdateMeals(listOf(breakfast, dinner), Instant.DISTANT_PAST)
            )

        assertEquals(0, events.size)
    }

    @Test
    fun `applying MealPlanInitializedEvent sets meals`() {
        val mealPlan = MealPlan()
        val defaultMeals = MealTemplates.forLanguage(Language.English)
        val event =
            MealPlanInitializedEvent(Language.English, defaultMeals, Instant.fromEpochSeconds(1))

        val updatedPlan = mealPlan.apply(event)

        assertEquals(defaultMeals, updatedPlan.meals)
    }

    @Test
    fun `applying MealAddedEvent adds meal`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val newMeal = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val event = MealAddedEvent(newMeal, Instant.fromEpochSeconds(1))

        val updatedPlan = mealPlan.apply(event)

        assertEquals(listOf(breakfast, newMeal), updatedPlan.meals)
    }

    @Test
    fun `applying MealUpdatedEvent updates meal`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val updatedBreakfast = breakfast.copy(name = "Better Breakfast")
        val event = MealUpdatedEvent(updatedBreakfast, Instant.fromEpochSeconds(1))

        val updatedPlan = mealPlan.apply(event)

        assertEquals(listOf(updatedBreakfast), updatedPlan.meals)
    }

    @Test
    fun `applying MealDeletedEvent removes meal`() {
        val mealPlan = MealPlan(meals = listOf(breakfast))
        val event = MealDeletedEvent(breakfast.id, Instant.fromEpochSeconds(1))

        val updatedPlan = mealPlan.apply(event)

        assertEquals(emptyList(), updatedPlan.meals)
    }

    @Test
    fun `aggregating event sequence produces correct state`() {
        val meal2 = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
        val updatedMeal2 = meal2.copy(name = "Better Lunch")
        val now = Instant.fromEpochSeconds(1)
        val events =
            listOf(
                MealPlanInitializedEvent(Language.English, listOf(breakfast), now),
                MealAddedEvent(meal2, now),
                MealUpdatedEvent(updatedMeal2, now),
                MealDeletedEvent(breakfast.id, now),
            )

        val mealPlan = events.toMealPlan()

        assertEquals(listOf(updatedMeal2), mealPlan.meals)
    }

    @Test
    fun `activeMeal returns active meal when time is within range`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val lunch = Meal(name = "Lunch", timeWindow = range("12:00", "14:00"))
        val plan = MealPlan(listOf(breakfast, lunch))

        assertEquals(breakfast, plan.activeMeal(LocalTime.parse("08:00"), 10.minutes))
    }

    @Test
    fun `activeMeal returns closest meal within threshold when no range matches time`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val lunch = Meal(name = "Lunch", timeWindow = range("12:00", "14:00"))
        val plan = MealPlan(listOf(breakfast, lunch))

        // 09:05 is 5 minutes from breakfast, within 10m threshold
        assertEquals(breakfast, plan.activeMeal(LocalTime.parse("09:05"), 10.minutes))

        // 11:55 is 5 minutes from lunch, within 10m threshold
        assertEquals(lunch, plan.activeMeal(LocalTime.parse("11:55"), 10.minutes))
    }

    @Test
    fun `activeMeal falls back to all day meal when range meal is outside threshold`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val allDay = Meal(name = "All Day", timeWindow = Meal.TimeWindow.AllDay)
        val plan = MealPlan(listOf(breakfast, allDay))

        // 10:00 is 1 hour from breakfast, outside 10m threshold
        assertEquals(allDay, plan.activeMeal(LocalTime.parse("10:00"), 10.minutes))
    }

    @Test
    fun `activeMeal falls back to all day meal when no range meal is close`() {
        val allDay = Meal(name = "All Day", timeWindow = Meal.TimeWindow.AllDay)
        val plan = MealPlan(listOf(allDay))

        assertEquals(allDay, plan.activeMeal(LocalTime.parse("15:00"), 10.minutes))
    }

    @Test
    fun `activeMeal falls back to first meal as last resort`() {
        val breakfast = Meal(name = "Breakfast", timeWindow = range("07:00", "09:00"))
        val plan = MealPlan(listOf(breakfast))

        assertEquals(breakfast, plan.activeMeal(LocalTime.parse("15:00"), 10.minutes))
    }

    private fun range(start: String, end: String) =
        Meal.TimeWindow.Range(LocalTime.parse(start), LocalTime.parse(end))
}
