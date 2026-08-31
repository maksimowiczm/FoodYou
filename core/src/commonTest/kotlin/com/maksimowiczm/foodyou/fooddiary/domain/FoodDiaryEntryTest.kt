package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
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
    private val composition =
        MeasuredFoodSnapshot(
            snapshot =
                LeafFoodSnapshot(
                    id = FoodSnapshotId.OpenFoodFacts("123"),
                    name = FoodName(fallback = "Apple"),
                    brand = null,
                    image = null,
                    nutritionFacts = NutritionFacts(),
                ),
            quantity =
                FoodSnapshotQuantity.Weight(
                    absoluteWeight = 100.grams,
                    servingWeight = null,
                    packageWeight = null,
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
            composition = composition,
            timestamp = timestamp,
            mealId = null,
        )
    private val entryWithMeal = entry.copy(mealId = mealId)

    @Test
    fun create_returns_created_event() {
        val now = Instant.fromEpochSeconds(2000)
        val clock = staticClock(now)

        val events =
            FoodDiaryEntry.create(
                id = diaryEntryId,
                profileIds = profileIds,
                composition = composition,
                mealId = mealId,
                timestamp = timestamp,
                clock = clock,
            )

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryCreatedEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
        assertEquals(profileIds, event.profileIds)
        assertEquals(composition, event.composition)
        assertEquals(mealId, event.mealId)
        assertEquals(timestamp, event.entryTimestamp)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun edit_returns_updated_event_when_changed() {
        val now = Instant.fromEpochSeconds(3000)
        val clock = staticClock(now)
        val updatedTimestamp = Instant.fromEpochSeconds(4000)
        val updatedProfileIds = setOf(ProfileId(Uuid.random()))

        val events =
            entry.edit(
                profileIds = updatedProfileIds,
                timestamp = updatedTimestamp,
                clock = clock,
            )

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryUpdatedEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
        assertEquals(updatedProfileIds, event.profileIds)
        assertEquals(composition, event.composition)
        assertNull(event.mealId)
        assertEquals(updatedTimestamp, event.entryTimestamp)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun edit_returns_updated_event_when_meal_id_changed() {
        val now = Instant.fromEpochSeconds(3500)
        val clock = staticClock(now)
        val newMealId = MealId(Uuid.random())

        val events =
            entryWithMeal.edit(
                mealId = newMealId,
                clock = clock,
            )

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryUpdatedEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
        assertEquals(newMealId, event.mealId)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun edit_returns_empty_list_when_not_changed() {
        val events = entry.edit()
        assertEquals(0, events.size)
    }

    @Test
    fun remove_returns_deleted_event() {
        val now = Instant.fromEpochSeconds(5000)
        val clock = staticClock(now)

        val events = entry.remove(DeleteStrategy.Delete, clock)

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryDeletedEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
        assertEquals(DeleteStrategy.Delete, event.strategy)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun unlinkFromMeal_returns_unlinked_event() {
        val now = Instant.fromEpochSeconds(5500)
        val clock = staticClock(now)

        val events = entryWithMeal.unlinkFromMeal(clock)

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryUnlinkedFromMealEvent>(events[0])
        assertEquals(diaryEntryId, event.diaryEntryId)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun apply_created_event() {
        val event =
            FoodDiaryEntryCreatedEvent(
                diaryEntryId = diaryEntryId,
                profileIds = profileIds,
                composition = composition,
                mealId = mealId,
                entryTimestamp = timestamp,
                timestamp = Instant.DISTANT_PAST,
            )
        val result = null.apply(event)
        assertEquals(entryWithMeal, result)
    }

    @Test
    fun apply_updated_event() {
        val updatedTimestamp = Instant.fromEpochSeconds(6000)
        val updatedProfileIds = setOf(ProfileId(Uuid.random()))
        val event =
            FoodDiaryEntryUpdatedEvent(
                diaryEntryId = diaryEntryId,
                profileIds = updatedProfileIds,
                composition = composition,
                mealId = mealId,
                entryTimestamp = updatedTimestamp,
                timestamp = Instant.DISTANT_PAST,
            )
        val result = entryWithMeal.apply(event)
        assertEquals(updatedProfileIds, result?.profileIds)
        assertEquals(composition, result?.composition)
        assertEquals(updatedTimestamp, result?.timestamp)
        assertEquals(mealId, result?.mealId)
    }

    @Test
    fun apply_deleted_event() {
        val event =
            FoodDiaryEntryDeletedEvent(diaryEntryId, DeleteStrategy.Delete, Instant.DISTANT_PAST)
        val result = entry.apply(event)
        assertNull(result)
    }

    @Test
    fun apply_unlinked_event_clears_meal_id() {
        val event = FoodDiaryEntryUnlinkedFromMealEvent(diaryEntryId, Instant.DISTANT_PAST)
        val result = entryWithMeal.apply(event)
        assertEquals(entryWithMeal.copy(mealId = null), result)
        assertNull(result?.mealId)
    }

    @Test
    fun apply_unlinked_event_on_null_returns_null() {
        val event = FoodDiaryEntryUnlinkedFromMealEvent(diaryEntryId, Instant.DISTANT_PAST)
        val result = null.apply(event)
        assertNull(result)
    }

    @Test
    fun toFoodDiaryEntry_reconstructs_state() {
        val updatedTimestamp = Instant.fromEpochSeconds(7000)
        val events =
            listOf(
                FoodDiaryEntryCreatedEvent(
                    diaryEntryId = diaryEntryId,
                    profileIds = profileIds,
                    composition = composition,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                    timestamp = Instant.fromEpochSeconds(1),
                ),
                FoodDiaryEntryUpdatedEvent(
                    diaryEntryId = diaryEntryId,
                    profileIds = profileIds,
                    composition = composition,
                    mealId = mealId,
                    entryTimestamp = updatedTimestamp,
                    timestamp = Instant.fromEpochSeconds(2),
                ),
            )

        val result = events.toFoodDiaryEntry()
        assertEquals(profileIds, result?.profileIds)
        assertEquals(composition, result?.composition)
        assertEquals(updatedTimestamp, result?.timestamp)
        assertEquals(mealId, result?.mealId)
    }

    @Test
    fun toFoodDiaryEntry_returns_null_if_deleted() {
        val events =
            listOf(
                FoodDiaryEntryCreatedEvent(
                    diaryEntryId = diaryEntryId,
                    profileIds = profileIds,
                    composition = composition,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                    timestamp = Instant.fromEpochSeconds(1),
                ),
                FoodDiaryEntryDeletedEvent(
                    diaryEntryId,
                    DeleteStrategy.Delete,
                    Instant.fromEpochSeconds(2),
                ),
            )

        val result = events.toFoodDiaryEntry()
        assertNull(result)
    }

    @Test
    fun toFoodDiaryEntry_reconstructs_state_after_unlink() {
        val events =
            listOf(
                FoodDiaryEntryCreatedEvent(
                    diaryEntryId = diaryEntryId,
                    profileIds = profileIds,
                    composition = composition,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                    timestamp = Instant.fromEpochSeconds(1),
                ),
                FoodDiaryEntryUnlinkedFromMealEvent(diaryEntryId, Instant.fromEpochSeconds(2)),
            )

        val result = events.toFoodDiaryEntry()
        assertEquals(entryWithMeal.copy(mealId = null), result)
        assertNull(result?.mealId)
    }
}
