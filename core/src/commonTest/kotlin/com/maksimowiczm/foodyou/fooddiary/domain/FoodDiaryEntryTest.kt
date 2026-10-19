package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.TrackedLeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class FoodDiaryEntryTest {
    private val diaryEntryId = FoodDiaryEntryId()
    private val measuredSnapshot =
        MeasuredFoodSnapshot(
            snapshot =
                TrackedLeafFoodSnapshot(
                    id = FoodSnapshotId.OpenFoodFacts("123"),
                    name = FoodName(fallback = "Apple"),
                    brand = null,
                    image = null,
                    nutritionFacts = NutritionFacts(),
                ),
            quantity =
                FoodSnapshotQuantity.Weight(
                    absoluteWeight = 100.grams,
                    servingQuantity = null,
                    packageQuantity = null,
                ),
        )
    private val timestamp = Instant.fromEpochSeconds(1000)
    private val profileId = ProfileId(Uuid.random())
    private val profileIds = setOf(profileId)
    private val mealId = MealId(Uuid.random())
    private val entry =
        FoodDiaryEntry(
            id = diaryEntryId,
            profileIds = profileIds,
            snapshot = measuredSnapshot,
            timestamp = timestamp,
            mealId = null,
        )
    private val entryWithMeal = entry.copy(mealId = mealId)

    @Test
    fun create_returns_created_event() {
        val events =
            (null as FoodDiaryEntry?).decide(
                FoodDiaryEntryCommand.Create(
                    id = diaryEntryId,
                    profileIds = profileIds,
                    snapshot = measuredSnapshot,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                )
            )

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryCreatedEntryEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
        assertEquals(profileIds, event.profileIds)
        assertEquals(measuredSnapshot, event.snapshot)
        assertEquals(mealId, event.mealId)
        assertEquals(timestamp, event.entryTimestamp)
    }

    @Test
    fun edit_returns_granular_events_when_changed() {
        val updatedTimestamp = Instant.fromEpochSeconds(4000)
        val updatedProfileIds = setOf(ProfileId(Uuid.random()))

        val events =
            entry.decide(
                FoodDiaryEntryCommand.Update(
                    transform = {
                        it.copy(profileIds = updatedProfileIds, timestamp = updatedTimestamp)
                    }
                )
            )

        assertEquals(2, events.size)
        val profileIdsEvent = assertIs<FoodDiaryEntryProfileIdsChangedEntryEvent>(events[0])
        assertEquals(updatedProfileIds, profileIdsEvent.profileIds)

        val timestampEvent = assertIs<FoodDiaryEntryTimestampChangedEntryEvent>(events[1])
        assertEquals(updatedTimestamp, timestampEvent.entryTimestamp)
    }

    @Test
    fun edit_returns_granular_events_when_snapshot_changed() {
        val newSnapshot =
            measuredSnapshot.copy(
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 200.grams,
                        servingQuantity = null,
                        packageQuantity = null,
                    )
            )

        val events =
            entry.decide(
                FoodDiaryEntryCommand.Update(transform = { it.copy(snapshot = newSnapshot) })
            )

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntrySnapshotChangedEntryEvent>(events[0])
        assertEquals(newSnapshot, event.snapshot)
    }

    @Test
    fun edit_returns_granular_events_when_meal_id_changed() {
        val newMealId = MealId(Uuid.random())

        val events =
            entryWithMeal.decide(
                FoodDiaryEntryCommand.Update(transform = { it.copy(mealId = newMealId) })
            )

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryMealLinkedEntryEvent>(events[0])
        assertEquals(newMealId, event.mealId)
    }

    @Test
    fun edit_returns_meal_unlinked_event_when_meal_id_cleared() {
        val events =
            entryWithMeal.decide(
                FoodDiaryEntryCommand.Update(transform = { it.copy(mealId = null) })
            )

        assertEquals(1, events.size)
        assertIs<FoodDiaryEntryMealUnlinkedEntryEvent>(events[0])
    }

    @Test
    fun edit_returns_empty_list_when_not_changed() {
        val events = entry.decide(FoodDiaryEntryCommand.Update { it })
        assertEquals(0, events.size)
    }

    @Test
    fun delete_returns_deleted_event() {
        val now = Instant.fromEpochSeconds(5000)

        val events = entry.decide(FoodDiaryEntryCommand.Delete)

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryDeletedEntryEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
    }

    @Test
    fun anonymize_returns_anonymized_event() {
        val now = Instant.fromEpochSeconds(5100)

        val events = entry.decide(FoodDiaryEntryCommand.Anonymize)

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryAnonymizedEntryEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
    }

    @Test
    fun unlinkFromMeal_returns_unlinked_event() {
        val now = Instant.fromEpochSeconds(5500)

        val events = entryWithMeal.decide(FoodDiaryEntryCommand.UnlinkFromMeal)

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryMealUnlinkedEntryEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
    }

    @Test
    fun apply_created_event() {
        val event =
            FoodDiaryEntryCreatedEntryEvent(
                diaryEntryId = diaryEntryId,
                profileIds = profileIds,
                snapshot = measuredSnapshot,
                mealId = mealId,
                entryTimestamp = timestamp,
            )
        val result = null.apply(event)
        assertEquals(entryWithMeal, result)
    }

    @Test
    fun apply_granular_events() {
        val updatedTimestamp = Instant.fromEpochSeconds(6000)
        val updatedProfileIds = setOf(ProfileId(Uuid.random()))
        val newMealId = MealId(Uuid.random())
        val newSnapshot =
            measuredSnapshot.copy(
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 200.grams,
                        servingQuantity = null,
                        packageQuantity = null,
                    )
            )

        var state: FoodDiaryEntry? = entry

        state =
            state.apply(
                FoodDiaryEntryProfileIdsChangedEntryEvent(
                    diaryEntryId,
                    updatedProfileIds,
                )
            )
        assertEquals(updatedProfileIds, state?.profileIds)

        state = state.apply(FoodDiaryEntrySnapshotChangedEntryEvent(diaryEntryId, newSnapshot))
        assertEquals(newSnapshot, state?.snapshot)

        state =
            state.apply(
                FoodDiaryEntryTimestampChangedEntryEvent(
                    diaryEntryId,
                    updatedTimestamp,
                )
            )
        assertEquals(updatedTimestamp, state?.timestamp)

        state = state.apply(FoodDiaryEntryMealLinkedEntryEvent(diaryEntryId, newMealId))
        assertEquals(newMealId, state?.mealId)

        state = state.apply(FoodDiaryEntryMealUnlinkedEntryEvent(diaryEntryId))
        assertNull(state?.mealId)
    }

    @Test
    fun apply_deleted_event() {
        val event = FoodDiaryEntryDeletedEntryEvent(diaryEntryId)
        val result = entry.apply(event)
        assertNull(result)
    }

    @Test
    fun apply_anonymized_event() {
        val event = FoodDiaryEntryAnonymizedEntryEvent(diaryEntryId)
        val result = entry.apply(event)
        assertNull(result)
    }

    @Test
    fun apply_unlinked_event_on_null_returns_null() {
        val event = FoodDiaryEntryMealUnlinkedEntryEvent(diaryEntryId)
        val result = null.apply(event)
        assertNull(result)
    }

    @Test
    fun toFoodDiaryEntry_reconstructs_state() {
        val updatedTimestamp = Instant.fromEpochSeconds(7000)
        val events =
            listOf(
                FoodDiaryEntryCreatedEntryEvent(
                    diaryEntryId = diaryEntryId,
                    profileIds = profileIds,
                    snapshot = measuredSnapshot,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                ),
                FoodDiaryEntryTimestampChangedEntryEvent(
                    diaryEntryId = diaryEntryId,
                    entryTimestamp = updatedTimestamp,
                ),
            )

        val result = events.toFoodDiaryEntry()
        assertEquals(profileIds, result?.profileIds)
        assertEquals(measuredSnapshot, result?.snapshot)
        assertEquals(updatedTimestamp, result?.timestamp)
        assertEquals(mealId, result?.mealId)
    }

    @Test
    fun toFoodDiaryEntry_returns_null_if_deleted() {
        val events =
            listOf(
                FoodDiaryEntryCreatedEntryEvent(
                    diaryEntryId = diaryEntryId,
                    profileIds = profileIds,
                    snapshot = measuredSnapshot,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                ),
                FoodDiaryEntryDeletedEntryEvent(diaryEntryId),
            )

        val result = events.toFoodDiaryEntry()
        assertNull(result)
    }

    @Test
    fun toFoodDiaryEntry_reconstructs_state_after_unlink() {
        val events =
            listOf(
                FoodDiaryEntryCreatedEntryEvent(
                    diaryEntryId = diaryEntryId,
                    profileIds = profileIds,
                    snapshot = measuredSnapshot,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                ),
                FoodDiaryEntryMealUnlinkedEntryEvent(diaryEntryId),
            )

        val result = events.toFoodDiaryEntry()
        assertEquals(entryWithMeal.copy(mealId = null), result)
        assertNull(result?.mealId)
    }
}
