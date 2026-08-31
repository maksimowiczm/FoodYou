@file:MustUseReturnValues

package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable data class FoodDiaryEntryId(val id: Uuid = Uuid.random())

@Serializable
data class FoodDiaryEntry(
    val id: FoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
    val snapshot: MeasuredFoodSnapshot,
    @Serializable(with = InstantComponentSerializer::class) val timestamp: Instant,
    val mealId: MealId?,
) {
    companion object {
        fun create(
            id: FoodDiaryEntryId,
            profileIds: Set<ProfileId>,
            snapshot: MeasuredFoodSnapshot,
            mealId: MealId,
            timestamp: Instant,
            clock: Clock = Clock.System,
        ): List<FoodDiaryEvent> =
            listOf(
                FoodDiaryEntryCreatedEvent(
                    diaryEntryId = id,
                    profileIds = profileIds,
                    snapshot = snapshot,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                    timestamp = clock.now(),
                )
            )
    }
}

fun FoodDiaryEntry.edit(
    profileIds: Set<ProfileId> = this.profileIds,
    snapshot: MeasuredFoodSnapshot = this.snapshot,
    mealId: MealId? = this.mealId,
    timestamp: Instant = this.timestamp,
    clock: Clock = Clock.System,
): List<FoodDiaryEvent> = buildList {
    if (profileIds != this@edit.profileIds) {
        add(FoodDiaryEntryProfileIdsChangedEvent(id, profileIds, clock.now()))
    }
    if (snapshot != this@edit.snapshot) {
        add(FoodDiaryEntrySnapshotChangedEvent(id, snapshot, clock.now()))
    }
    if (timestamp != this@edit.timestamp) {
        add(FoodDiaryEntryTimestampChangedEvent(id, timestamp, clock.now()))
    }
    if (mealId != this@edit.mealId) {
        if (mealId != null) {
            add(FoodDiaryEntryMealLinkedEvent(id, mealId, clock.now()))
        } else {
            add(FoodDiaryEntryMealUnlinkedEvent(id, clock.now()))
        }
    }
}

fun FoodDiaryEntry.remove(
    strategy: DeleteStrategy,
    clock: Clock = Clock.System,
): List<FoodDiaryEvent> =
    listOf(
        FoodDiaryEntryDeletedEvent(
            diaryEntryId = id,
            strategy = strategy,
            timestamp = clock.now(),
        )
    )

fun FoodDiaryEntry.unlinkFromMeal(clock: Clock = Clock.System): List<FoodDiaryEvent> =
    listOf(
        FoodDiaryEntryMealUnlinkedEvent(
            diaryEntryId = id,
            timestamp = clock.now(),
        )
    )

fun FoodDiaryEntry?.apply(event: FoodDiaryEvent): FoodDiaryEntry? =
    when (event) {
        is FoodDiaryEntryCreatedEvent ->
            FoodDiaryEntry(
                id = event.diaryEntryId,
                profileIds = event.profileIds,
                snapshot = event.snapshot,
                timestamp = event.entryTimestamp,
                mealId = event.mealId,
            )

        is FoodDiaryEntryProfileIdsChangedEvent -> this?.copy(profileIds = event.profileIds)

        is FoodDiaryEntrySnapshotChangedEvent -> this?.copy(snapshot = event.snapshot)

        is FoodDiaryEntryTimestampChangedEvent -> this?.copy(timestamp = event.entryTimestamp)

        is FoodDiaryEntryMealLinkedEvent -> this?.copy(mealId = event.mealId)

        is FoodDiaryEntryMealUnlinkedEvent -> this?.copy(mealId = null)

        is FoodDiaryEntryDeletedEvent -> null
    }

fun Iterable<FoodDiaryEvent>.toFoodDiaryEntry(): FoodDiaryEntry? =
    fold(null) { state, event -> state.apply(event) }
