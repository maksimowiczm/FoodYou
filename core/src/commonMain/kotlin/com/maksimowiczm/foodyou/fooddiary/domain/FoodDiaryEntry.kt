@file:MustUseReturnValues

package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.Decider
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
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

fun FoodDiaryEntry?.decide(command: FoodDiaryEntryCommand): List<FoodDiaryEntryEvent> =
    when (command) {
        is FoodDiaryEntryCommand.Create ->
            if (this == null) {
                require(command.profileIds.isNotEmpty()) { "Profile ids cannot be empty" }
                listOf(
                    FoodDiaryEntryCreatedEntryEvent(
                        diaryEntryId = command.id,
                        profileIds = command.profileIds,
                        snapshot = command.snapshot,
                        mealId = command.mealId,
                        entryTimestamp = command.entryTimestamp,
                    )
                )
            } else {
                emptyList()
            }

        is FoodDiaryEntryCommand.ImportFoodYou3 ->
            if (this == null) {
                listOf(
                    FoodDiaryEntryImportedFoodYou3Event(
                        diaryEntryId = command.id,
                        profileIds = setOf(command.profileId),
                        snapshot = command.snapshot,
                        mealId = command.mealId,
                        entryDate = command.entryDate,
                    )
                )
            } else {
                emptyList()
            }

        is FoodDiaryEntryCommand.Update ->
            this?.let { entry ->
                val updated = command.transform(entry)
                if (updated == null) {
                    listOf(FoodDiaryEntryDeletedEntryEvent(diaryEntryId = entry.id))
                } else {
                    require(updated.profileIds.isNotEmpty()) { "Profile ids cannot be empty" }
                    buildList {
                        if (updated.profileIds != entry.profileIds) {
                            add(
                                FoodDiaryEntryProfileIdsChangedEntryEvent(
                                    diaryEntryId = entry.id,
                                    profileIds = updated.profileIds,
                                )
                            )
                        }
                        if (updated.snapshot != entry.snapshot) {
                            add(
                                FoodDiaryEntrySnapshotChangedEntryEvent(
                                    diaryEntryId = entry.id,
                                    snapshot = updated.snapshot,
                                )
                            )
                        }
                        if (updated.timestamp != entry.timestamp) {
                            add(
                                FoodDiaryEntryTimestampChangedEntryEvent(
                                    diaryEntryId = entry.id,
                                    entryTimestamp = updated.timestamp,
                                )
                            )
                        }
                        if (updated.mealId != entry.mealId) {
                            if (updated.mealId != null) {
                                add(
                                    FoodDiaryEntryMealLinkedEntryEvent(
                                        diaryEntryId = entry.id,
                                        mealId = updated.mealId,
                                    )
                                )
                            } else {
                                add(FoodDiaryEntryMealUnlinkedEntryEvent(diaryEntryId = entry.id))
                            }
                        }
                    }
                }
            } ?: emptyList()

        is FoodDiaryEntryCommand.Anonymize ->
            this?.let { entry ->
                listOf(FoodDiaryEntryAnonymizedEntryEvent(diaryEntryId = entry.id))
            } ?: emptyList()

        is FoodDiaryEntryCommand.UnlinkFromMeal ->
            this?.let { entry ->
                listOf(FoodDiaryEntryMealUnlinkedEntryEvent(diaryEntryId = entry.id))
            } ?: emptyList()
    }

fun FoodDiaryEntry?.apply(event: FoodDiaryEntryEvent): FoodDiaryEntry? =
    when (event) {
        is FoodDiaryEntryCreatedEntryEvent ->
            FoodDiaryEntry(
                id = event.diaryEntryId,
                profileIds = event.profileIds,
                snapshot = event.snapshot,
                timestamp = event.entryTimestamp,
                mealId = event.mealId,
            )

        is FoodDiaryEntryImportedFoodYou3Event ->
            FoodDiaryEntry(
                id = event.diaryEntryId,
                profileIds = event.profileIds,
                snapshot = event.snapshot,
                timestamp = event.entryDate.atStartOfDayIn(TimeZone.currentSystemDefault()),
                mealId = event.mealId,
            )

        is FoodDiaryEntryProfileIdsChangedEntryEvent -> this?.copy(profileIds = event.profileIds)

        is FoodDiaryEntrySnapshotChangedEntryEvent -> this?.copy(snapshot = event.snapshot)

        is FoodDiaryEntryTimestampChangedEntryEvent -> this?.copy(timestamp = event.entryTimestamp)

        is FoodDiaryEntryMealLinkedEntryEvent -> this?.copy(mealId = event.mealId)

        is FoodDiaryEntryMealUnlinkedEntryEvent -> this?.copy(mealId = null)

        is FoodDiaryEntryAnonymizedEntryEvent -> null

        is FoodDiaryEntryDeletedEntryEvent -> null
    }

fun Iterable<FoodDiaryEntryEvent>.toFoodDiaryEntry(): FoodDiaryEntry? =
    fold(null) { state, event -> state.apply(event) }

val foodDiaryDecider =
    Decider<FoodDiaryEntryCommand, FoodDiaryEntryEvent, FoodDiaryEntry?>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = null,
    )
