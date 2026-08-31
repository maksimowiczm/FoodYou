package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class FoodDiaryEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class FoodDiaryEntryCreatedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
    val snapshot: MeasuredFoodSnapshot,
    val mealId: MealId,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryProfileIdsChangedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntrySnapshotChangedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val snapshot: MeasuredFoodSnapshot,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryTimestampChangedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryMealLinkedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val mealId: MealId,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryMealUnlinkedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryDeletedEvent(
    val diaryEntryId: FoodDiaryEntryId,
    val strategy: DeleteStrategy,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()
