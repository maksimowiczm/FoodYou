package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class FoodDiaryEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class FoodDiaryEntryCreatedEvent(
    val identity: FoodDiaryEntryIdentity,
    val profileId: ProfileId,
    val composition: FoodCompositionComponent,
    val mealIdentity: MealIdentity,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryUpdatedEvent(
    val identity: FoodDiaryEntryIdentity,
    val composition: FoodCompositionComponent,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryUnlinkedFromMealEvent(
    val identity: FoodDiaryEntryIdentity,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryDeletedEvent(
    val identity: FoodDiaryEntryIdentity,
    val strategy: DeleteStrategy,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()
