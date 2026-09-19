package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed interface FoodDiaryEvent : DomainEvent

@Serializable
data class FoodDiaryEntryCreatedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
    val snapshot: MeasuredFoodSnapshot,
    val mealId: MealId,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
) : FoodDiaryEvent

@Serializable
data class FoodDiaryEntryProfileIdsChangedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
) : FoodDiaryEvent

@Serializable
data class FoodDiaryEntrySnapshotChangedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val snapshot: MeasuredFoodSnapshot,
) : FoodDiaryEvent

@Serializable
data class FoodDiaryEntryTimestampChangedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
) : FoodDiaryEvent

@Serializable
data class FoodDiaryEntryMealLinkedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val mealId: MealId,
) : FoodDiaryEvent

@Serializable
data class FoodDiaryEntryMealUnlinkedEvent(val diaryEntryId: FoodDiaryEntryId) : FoodDiaryEvent

@Serializable
data class FoodDiaryEntryAnonymizedEvent(val diaryEntryId: FoodDiaryEntryId) : FoodDiaryEvent

@Serializable
data class FoodDiaryEntryDeletedEvent(val diaryEntryId: FoodDiaryEntryId) : FoodDiaryEvent
