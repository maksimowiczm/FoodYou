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
    val composition: MeasuredFoodSnapshot,
    @Serializable(with = InstantComponentSerializer::class) val timestamp: Instant,
    val mealId: MealId?,
) {
    companion object {
        fun create(
            id: FoodDiaryEntryId,
            profileIds: Set<ProfileId>,
            composition: MeasuredFoodSnapshot,
            mealId: MealId,
            timestamp: Instant,
            clock: Clock = Clock.System,
        ): List<FoodDiaryEvent> =
            listOf(
                FoodDiaryEntryCreatedEvent(
                    diaryEntryId = id,
                    profileIds = profileIds,
                    composition = composition,
                    mealId = mealId,
                    entryTimestamp = timestamp,
                    timestamp = clock.now(),
                )
            )
    }
}

fun FoodDiaryEntry.edit(
    profileIds: Set<ProfileId> = this.profileIds,
    composition: MeasuredFoodSnapshot = this.composition,
    mealId: MealId? = this.mealId,
    timestamp: Instant = this.timestamp,
    clock: Clock = Clock.System,
): List<FoodDiaryEvent> = buildList {
    val updated =
        this@edit.copy(
            profileIds = profileIds,
            composition = composition,
            mealId = mealId,
            timestamp = timestamp,
        )
    if (updated != this@edit)
        add(
            FoodDiaryEntryUpdatedEvent(
                diaryEntryId = id,
                profileIds = profileIds,
                composition = composition,
                mealId = mealId,
                entryTimestamp = timestamp,
                timestamp = clock.now(),
            )
        )
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
        FoodDiaryEntryUnlinkedFromMealEvent(
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
                composition = event.composition,
                timestamp = event.entryTimestamp,
                mealId = event.mealId,
            )
        is FoodDiaryEntryUpdatedEvent ->
            this?.copy(
                profileIds = event.profileIds,
                composition = event.composition,
                timestamp = event.entryTimestamp,
                mealId = event.mealId,
            )
        is FoodDiaryEntryDeletedEvent -> null
        is FoodDiaryEntryUnlinkedFromMealEvent -> this?.copy(mealId = null)
    }

fun Iterable<FoodDiaryEvent>.toFoodDiaryEntry(): FoodDiaryEntry? =
    fold(null) { state, event -> state.apply(event) }
