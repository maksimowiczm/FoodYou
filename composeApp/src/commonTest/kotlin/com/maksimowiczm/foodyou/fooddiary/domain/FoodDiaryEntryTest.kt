package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
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
    private val entry =
        FoodDiaryEntry(identity = identity, composition = composition, timestamp = timestamp)

    @Test
    fun create_returns_created_event() {
        val now = Instant.fromEpochSeconds(2000)
        val clock = staticClock(now)

        val events = FoodDiaryEntry.create(entry, clock)

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryCreatedEvent>(events[0])
        assertEquals(entry, event.entry)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun update_returns_updated_event_when_changed() {
        val now = Instant.fromEpochSeconds(3000)
        val clock = staticClock(now)
        val updatedTimestamp = Instant.fromEpochSeconds(4000)

        val events = entry.update(clock) { it.copy(timestamp = updatedTimestamp) }

        assertEquals(1, events.size)
        val event = assertIs<FoodDiaryEntryUpdatedEvent>(events[0])
        assertEquals(updatedTimestamp, event.entry.timestamp)
        assertEquals(now, event.timestamp)
    }

    @Test
    fun update_returns_empty_list_when_not_changed() {
        val events = entry.update { it }
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
    fun apply_created_event() {
        val event = FoodDiaryEntryCreatedEvent(entry, Instant.DISTANT_PAST)
        val result = null.apply(event)
        assertEquals(entry, result)
    }

    @Test
    fun apply_updated_event() {
        val updatedEntry = entry.copy(timestamp = Instant.fromEpochSeconds(6000))
        val event = FoodDiaryEntryUpdatedEvent(updatedEntry, Instant.DISTANT_PAST)
        val result = entry.apply(event)
        assertEquals(updatedEntry, result)
    }

    @Test
    fun apply_deleted_event() {
        val event =
            FoodDiaryEntryDeletedEvent(identity, DeleteStrategy.Delete, Instant.DISTANT_PAST)
        val result = entry.apply(event)
        assertNull(result)
    }

    @Test
    fun toFoodDiaryEntry_reconstructs_state() {
        val updatedEntry = entry.copy(timestamp = Instant.fromEpochSeconds(7000))
        val events =
            listOf(
                FoodDiaryEntryCreatedEvent(entry, Instant.fromEpochSeconds(1)),
                FoodDiaryEntryUpdatedEvent(updatedEntry, Instant.fromEpochSeconds(2)),
            )

        val result = events.toFoodDiaryEntry()
        assertEquals(updatedEntry, result)
    }

    @Test
    fun toFoodDiaryEntry_returns_null_if_deleted() {
        val events =
            listOf(
                FoodDiaryEntryCreatedEvent(entry, Instant.fromEpochSeconds(1)),
                FoodDiaryEntryDeletedEvent(
                    identity,
                    DeleteStrategy.Delete,
                    Instant.fromEpochSeconds(2),
                ),
            )

        val result = events.toFoodDiaryEntry()
        assertNull(result)
    }
}
