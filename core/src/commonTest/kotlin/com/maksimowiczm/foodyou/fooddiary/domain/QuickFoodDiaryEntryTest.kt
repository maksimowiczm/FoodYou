package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class QuickFoodDiaryEntryTest {
    private val entryId = QuickFoodDiaryEntryId()
    private val profileId = ProfileId(Uuid.random())
    private val profileIds = setOf(profileId)
    private val nutritionFacts = NutritionFacts()
    private val mealId = MealId(Uuid.random())
    private val timestamp = Instant.fromEpochSeconds(1000)

    private val entry =
        QuickFoodDiaryEntry(
            id = entryId,
            profileIds = profileIds,
            name = "Quick Food",
            nutritionFacts = nutritionFacts,
            timestamp = timestamp,
            mealId = mealId,
        )

    @Test
    fun create_returns_created_event() {
        val events =
            (null as QuickFoodDiaryEntry?).decide(
                QuickFoodDiaryEntryCommand.Create(
                    id = entryId,
                    profileIds = profileIds,
                    name = "Quick Food",
                    nutritionFacts = nutritionFacts,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                )
            )

        assertEquals(1, events.size)
        val event = assertIs<QuickFoodDiaryEntryCreatedEntryEvent>(events[0])
        assertEquals(entryId, event.diaryEntryId)
        assertEquals(profileIds, event.profileIds)
        assertEquals(nutritionFacts, event.nutritionFacts)
        assertEquals(mealId, event.mealId)
        assertEquals(timestamp, event.entryTimestamp)
    }

    @Test
    fun link_to_meal_returns_event() {
        val newMealId = MealId(Uuid.random())
        val events = entry.decide(QuickFoodDiaryEntryCommand.LinkToMeal(newMealId))

        assertEquals(1, events.size)
        val event = assertIs<QuickFoodDiaryEntryMealLinkedEntryEvent>(events[0])
        assertEquals(entryId, event.diaryEntryId)
        assertEquals(newMealId, event.mealId)
    }

    @Test
    fun unlink_from_meal_returns_event() {
        val events = entry.decide(QuickFoodDiaryEntryCommand.UnlinkFromMeal)

        assertEquals(1, events.size)
        val event = assertIs<QuickFoodDiaryEntryMealUnlinkedEntryEvent>(events[0])
        assertEquals(entryId, event.diaryEntryId)
    }

    @Test
    fun delete_returns_event() {
        val events = entry.decide(QuickFoodDiaryEntryCommand.Delete)

        assertEquals(1, events.size)
        val event = assertIs<QuickFoodDiaryEntryDeletedEntryEvent>(events[0])
        assertEquals(entryId, event.diaryEntryId)
    }

    @Test
    fun apply_events_reconstructs_entry() {
        val events =
            listOf(
                QuickFoodDiaryEntryCreatedEntryEvent(
                    diaryEntryId = entryId,
                    profileIds = profileIds,
                    name = "Quick Food",
                    nutritionFacts = nutritionFacts,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                ),
                QuickFoodDiaryEntryNutritionFactsChangedEntryEvent(
                    diaryEntryId = entryId,
                    nutritionFacts = NutritionFacts(),
                ),
                QuickFoodDiaryEntryMealUnlinkedEntryEvent(entryId),
                QuickFoodDiaryEntryDeletedEntryEvent(entryId),
            )

        val reconstructed = events.toAnonymousFoodDiaryEntry()
        assertNull(reconstructed)
    }
}
