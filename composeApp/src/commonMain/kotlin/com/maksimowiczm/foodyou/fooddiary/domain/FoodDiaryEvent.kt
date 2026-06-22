package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class FoodDiaryEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class FoodDiaryEntryCreatedEvent(
    val entry: FoodDiaryEntry,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryUpdatedEvent(
    val entry: FoodDiaryEntry,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()

@Serializable
data class FoodDiaryEntryDeletedEvent(
    val identity: FoodDiaryEntryIdentity,
    val strategy: DeleteStrategy,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDiaryEvent()
