package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class FoodDiaryEntryTest {
    private val identity = FoodDiaryEntryIdentity(Uuid.random())
    private val composition =
        FoodCompositionComponent.Simple(
            identity = FoodCompositionComponentIdentity.OpenFoodFacts("123"),
            name = FoodName(fallback = "Apple"),
            image = null,
            nutritionFacts = NutritionFacts(),
            quantity =
                FoodComponentComponentQuantity.Weight(
                    absoluteWeight = 100.grams,
                    servingWeight = null,
                    packageWeight = null,
                ),
        )
    private val timestamp = Instant.fromEpochSeconds(1000)
    private val profileId = ProfileId(Uuid.random())
    private val profileIds = setOf(profileId)
    private val mealIdentity = MealIdentity(Uuid.random())
    private val entry =
        FoodDiaryEntry(
            identity = identity,
            profileIds = profileIds,
            composition = composition,
            timestamp = timestamp,
            mealIdentity = null,
        )
    private val entryWithMeal = entry.copy(mealIdentity = mealIdentity)

    @Test
    fun create_returns_created_event() {
        val now = Instant.fromEpochSeconds(2000)
        val clock = staticClock(now)

        val events =
            FoodDiaryEntry.create(
                identity = identity,
                profileIds = profileIds,
                composition = composition,
                mealIdentity = mealIdentity,
                timestamp = timestamp,
                clock = clock,
            )

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryCreatedEvent>(events[0])
        assertEquals(identity, event.identity)
        assertEquals(profileIds, event.profileIds)
        assertEquals(composition, event.composition)
        assertEquals(mealIdentity, event.mealIdentity)
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
        assertEquals(identity, event.identity)
        assertEquals(updatedProfileIds, event.profileIds)
        assertEquals(composition, event.composition)
        assertNull(event.mealIdentity)
        assertEquals(updatedTimestamp, event.entryTimestamp)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun edit_returns_updated_event_when_meal_identity_changed() {
        val now = Instant.fromEpochSeconds(3500)
        val clock = staticClock(now)
        val newMealIdentity = MealIdentity(Uuid.random())

        val events =
            entryWithMeal.edit(
                mealIdentity = newMealIdentity,
                clock = clock,
            )

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryUpdatedEvent>(events[0])
        assertEquals(identity, event.identity)
        assertEquals(newMealIdentity, event.mealIdentity)
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
        assertEquals(identity, event.identity)
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
        assertEquals(identity, event.identity)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun apply_created_event() {
        val event =
            FoodDiaryEntryCreatedEvent(
                identity = identity,
                profileIds = profileIds,
                composition = composition,
                mealIdentity = mealIdentity,
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
                identity = identity,
                profileIds = updatedProfileIds,
                composition = composition,
                mealIdentity = mealIdentity,
                entryTimestamp = updatedTimestamp,
                timestamp = Instant.DISTANT_PAST,
            )
        val result = entryWithMeal.apply(event)
        assertEquals(updatedProfileIds, result?.profileIds)
        assertEquals(composition, result?.composition)
        assertEquals(updatedTimestamp, result?.timestamp)
        assertEquals(mealIdentity, result?.mealIdentity)
    }

    @Test
    fun apply_deleted_event() {
        val event =
            FoodDiaryEntryDeletedEvent(identity, DeleteStrategy.Delete, Instant.DISTANT_PAST)
        val result = entry.apply(event)
        assertNull(result)
    }

    @Test
    fun apply_unlinked_event_clears_meal_identity() {
        val event = FoodDiaryEntryUnlinkedFromMealEvent(identity, Instant.DISTANT_PAST)
        val result = entryWithMeal.apply(event)
        assertEquals(entryWithMeal.copy(mealIdentity = null), result)
        assertNull(result?.mealIdentity)
    }

    @Test
    fun apply_unlinked_event_on_null_returns_null() {
        val event = FoodDiaryEntryUnlinkedFromMealEvent(identity, Instant.DISTANT_PAST)
        val result = null.apply(event)
        assertNull(result)
    }

    @Test
    fun toFoodDiaryEntry_reconstructs_state() {
        val updatedTimestamp = Instant.fromEpochSeconds(7000)
        val events =
            listOf(
                FoodDiaryEntryCreatedEvent(
                    identity = identity,
                    profileIds = profileIds,
                    composition = composition,
                    mealIdentity = mealIdentity,
                    entryTimestamp = timestamp,
                    timestamp = Instant.fromEpochSeconds(1),
                ),
                FoodDiaryEntryUpdatedEvent(
                    identity = identity,
                    profileIds = profileIds,
                    composition = composition,
                    mealIdentity = mealIdentity,
                    entryTimestamp = updatedTimestamp,
                    timestamp = Instant.fromEpochSeconds(2),
                ),
            )

        val result = events.toFoodDiaryEntry()
        assertEquals(profileIds, result?.profileIds)
        assertEquals(composition, result?.composition)
        assertEquals(updatedTimestamp, result?.timestamp)
        assertEquals(mealIdentity, result?.mealIdentity)
    }

    @Test
    fun toFoodDiaryEntry_returns_null_if_deleted() {
        val events =
            listOf(
                FoodDiaryEntryCreatedEvent(
                    identity = identity,
                    profileIds = profileIds,
                    composition = composition,
                    mealIdentity = mealIdentity,
                    entryTimestamp = timestamp,
                    timestamp = Instant.fromEpochSeconds(1),
                ),
                FoodDiaryEntryDeletedEvent(
                    identity,
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
                    identity = identity,
                    profileIds = profileIds,
                    composition = composition,
                    mealIdentity = mealIdentity,
                    entryTimestamp = timestamp,
                    timestamp = Instant.fromEpochSeconds(1),
                ),
                FoodDiaryEntryUnlinkedFromMealEvent(identity, Instant.fromEpochSeconds(2)),
            )

        val result = events.toFoodDiaryEntry()
        assertEquals(entryWithMeal.copy(mealIdentity = null), result)
        assertNull(result?.mealIdentity)
    }
}
