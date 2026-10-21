package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed interface FoodDiaryEntryEvent : DomainEvent

@Serializable
data class FoodDiaryEntryCreatedEntryEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
    val snapshot: MeasuredFoodSnapshot,
    val mealId: MealId,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
) : FoodDiaryEntryEvent

@Serializable
data class FoodDiaryEntryImportedFoodYou3Event(
    val diaryEntryId: FoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
    val snapshot: MeasuredFoodSnapshot,
    val mealId: MealId?,
    val entryDate: LocalDate,
) : FoodDiaryEntryEvent

@Serializable
data class FoodDiaryEntryProfileIdsChangedEntryEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
) : FoodDiaryEntryEvent

@Serializable
data class FoodDiaryEntrySnapshotChangedEntryEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val snapshot: MeasuredFoodSnapshot,
) : FoodDiaryEntryEvent

@Serializable
data class FoodDiaryEntryTimestampChangedEntryEvent(
    val diaryEntryId: FoodDiaryEntryId,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
) : FoodDiaryEntryEvent

@Serializable
data class FoodDiaryEntryMealLinkedEntryEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val mealId: MealId,
) : FoodDiaryEntryEvent

@Serializable
data class FoodDiaryEntryMealUnlinkedEntryEvent(val diaryEntryId: FoodDiaryEntryId) :
    FoodDiaryEntryEvent

@Serializable
data class FoodDiaryEntryAnonymizedEntryEvent(val diaryEntryId: FoodDiaryEntryId) :
    FoodDiaryEntryEvent

@Serializable
data class FoodDiaryEntryDeletedEntryEvent(val diaryEntryId: FoodDiaryEntryId) : FoodDiaryEntryEvent
