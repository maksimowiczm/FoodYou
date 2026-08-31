@file:MustUseReturnValues

package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable data class FoodDiaryEntryIdentity(val id: Uuid)

@Serializable
data class FoodDiaryEntry(
    val identity: FoodDiaryEntryIdentity,
    val profileIds: Set<ProfileId>,
    val composition: MeasuredFoodSnapshot,
    @Serializable(with = InstantComponentSerializer::class) val timestamp: Instant,
    val mealIdentity: MealIdentity?,
) {
    companion object {
        fun create(
            identity: FoodDiaryEntryIdentity,
            profileIds: Set<ProfileId>,
            composition: MeasuredFoodSnapshot,
            mealIdentity: MealIdentity,
            timestamp: Instant,
            clock: Clock = Clock.System,
        ): List<FoodDiaryEvent> =
            listOf(
                FoodDiaryEntryCreatedEvent(
                    identity = identity,
                    profileIds = profileIds,
                    composition = composition,
                    mealIdentity = mealIdentity,
                    entryTimestamp = timestamp,
                    timestamp = clock.now(),
                )
            )
    }
}

fun FoodDiaryEntry.edit(
    profileIds: Set<ProfileId> = this.profileIds,
    composition: MeasuredFoodSnapshot = this.composition,
    mealIdentity: MealIdentity? = this.mealIdentity,
    timestamp: Instant = this.timestamp,
    clock: Clock = Clock.System,
): List<FoodDiaryEvent> = buildList {
    val updated =
        this@edit.copy(
            profileIds = profileIds,
            composition = composition,
            mealIdentity = mealIdentity,
            timestamp = timestamp,
        )
    if (updated != this@edit)
        add(
            FoodDiaryEntryUpdatedEvent(
                identity = identity,
                profileIds = profileIds,
                composition = composition,
                mealIdentity = mealIdentity,
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
            identity = identity,
            strategy = strategy,
            timestamp = clock.now(),
        )
    )

fun FoodDiaryEntry.unlinkFromMeal(clock: Clock = Clock.System): List<FoodDiaryEvent> =
    listOf(
        FoodDiaryEntryUnlinkedFromMealEvent(
            identity = identity,
            timestamp = clock.now(),
        )
    )

fun FoodDiaryEntry?.apply(event: FoodDiaryEvent): FoodDiaryEntry? =
    when (event) {
        is FoodDiaryEntryCreatedEvent ->
            FoodDiaryEntry(
                identity = event.identity,
                profileIds = event.profileIds,
                composition = event.composition,
                timestamp = event.entryTimestamp,
                mealIdentity = event.mealIdentity,
            )
        is FoodDiaryEntryUpdatedEvent ->
            this?.copy(
                profileIds = event.profileIds,
                composition = event.composition,
                timestamp = event.entryTimestamp,
                mealIdentity = event.mealIdentity,
            )
        is FoodDiaryEntryDeletedEvent -> null
        is FoodDiaryEntryUnlinkedFromMealEvent -> this?.copy(mealIdentity = null)
    }

fun Iterable<FoodDiaryEvent>.toFoodDiaryEntry(): FoodDiaryEntry? =
    fold(null) { state, event -> state.apply(event) }
