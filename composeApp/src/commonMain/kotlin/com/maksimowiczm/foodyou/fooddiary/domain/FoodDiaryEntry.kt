@file:MustUseReturnValues

package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable data class FoodDiaryEntryIdentity(val id: Uuid)

@Serializable
data class FoodDiaryEntry(
    val identity: FoodDiaryEntryIdentity,
    val composition: FoodCompositionComponent,
    @Serializable(with = InstantComponentSerializer::class) val timestamp: Instant,
) {
    companion object {
        fun create(entry: FoodDiaryEntry, clock: Clock = Clock.System): List<FoodDiaryEvent> =
            listOf(FoodDiaryEntryCreatedEvent(entry = entry, timestamp = clock.now()))
    }
}

inline fun FoodDiaryEntry.update(
    clock: Clock = Clock.System,
    transform: (FoodDiaryEntry) -> FoodDiaryEntry,
): List<FoodDiaryEvent> = buildList {
    val updated = transform(this@update)
    if (updated != this@update)
        add(FoodDiaryEntryUpdatedEvent(entry = updated, timestamp = clock.now()))
}

fun FoodDiaryEntry.remove(
    strategy: DeleteStrategy,
    clock: Clock = Clock.System,
): List<FoodDiaryEvent> =
    listOf(
        FoodDiaryEntryDeletedEvent(
            identity = identity,
            strategy = strategy,
            timestamp = clock.now(),
        )
    )

fun FoodDiaryEntry?.apply(event: FoodDiaryEvent): FoodDiaryEntry? =
    when (event) {
        is FoodDiaryEntryCreatedEvent -> event.entry
        is FoodDiaryEntryUpdatedEvent -> event.entry
        is FoodDiaryEntryDeletedEvent -> null
    }

fun Iterable<FoodDiaryEvent>.toFoodDiaryEntry(): FoodDiaryEntry? =
    fold(null) { state, event -> state.apply(event) }
