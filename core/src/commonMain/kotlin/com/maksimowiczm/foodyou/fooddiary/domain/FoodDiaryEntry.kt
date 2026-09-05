@file:MustUseReturnValues

package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.Decider
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.mealplan.domain.MealId
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
)

fun FoodDiaryEntry?.decide(command: FoodDiaryCommand): List<FoodDiaryEvent> =
    when (command) {
        is FoodDiaryCommand.Create ->
            if (this == null) {
                listOf(
                    FoodDiaryEntryCreatedEvent(
                        diaryEntryId = command.id,
                        profileIds = command.profileIds,
                        snapshot = command.snapshot,
                        mealId = command.mealId,
                        entryTimestamp = command.entryTimestamp,
                        timestamp = command.timestamp,
                    )
                )
            } else {
                emptyList()
            }

        is FoodDiaryCommand.Update ->
            this?.let { entry ->
                val updated = command.transform(entry)
                if (updated == null) {
                    listOf(
                        FoodDiaryEntryDeletedEvent(
                            diaryEntryId = entry.id,
                            strategy = DeleteStrategy.Delete,
                            timestamp = command.timestamp,
                        )
                    )
                } else {
                    buildList {
                        if (updated.profileIds != entry.profileIds) {
                            add(
                                FoodDiaryEntryProfileIdsChangedEvent(
                                    diaryEntryId = entry.id,
                                    profileIds = updated.profileIds,
                                    timestamp = command.timestamp,
                                )
                            )
                        }
                        if (updated.snapshot != entry.snapshot) {
                            add(
                                FoodDiaryEntrySnapshotChangedEvent(
                                    diaryEntryId = entry.id,
                                    snapshot = updated.snapshot,
                                    timestamp = command.timestamp,
                                )
                            )
                        }
                        if (updated.timestamp != entry.timestamp) {
                            add(
                                FoodDiaryEntryTimestampChangedEvent(
                                    diaryEntryId = entry.id,
                                    entryTimestamp = updated.timestamp,
                                    timestamp = command.timestamp,
                                )
                            )
                        }
                        if (updated.mealId != entry.mealId) {
                            if (updated.mealId != null) {
                                add(
                                    FoodDiaryEntryMealLinkedEvent(
                                        diaryEntryId = entry.id,
                                        mealId = updated.mealId,
                                        timestamp = command.timestamp,
                                    )
                                )
                            } else {
                                add(
                                    FoodDiaryEntryMealUnlinkedEvent(
                                        diaryEntryId = entry.id,
                                        timestamp = command.timestamp,
                                    )
                                )
                            }
                        }
                    }
                }
            } ?: emptyList()

        is FoodDiaryCommand.Remove ->
            this?.let { entry ->
                listOf(
                    FoodDiaryEntryDeletedEvent(
                        diaryEntryId = entry.id,
                        strategy = command.strategy,
                        timestamp = command.timestamp,
                    )
                )
            } ?: emptyList()

        is FoodDiaryCommand.UnlinkFromMeal ->
            this?.let { entry ->
                listOf(
                    FoodDiaryEntryMealUnlinkedEvent(
                        diaryEntryId = entry.id,
                        timestamp = command.timestamp,
                    )
                )
            } ?: emptyList()
    }

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

val foodDiaryDecider =
    Decider<FoodDiaryCommand, FoodDiaryEvent, FoodDiaryEntry?>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = null,
    )
