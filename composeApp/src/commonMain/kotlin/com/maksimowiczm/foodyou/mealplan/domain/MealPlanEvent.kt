package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class MealPlanEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class MealAddedEvent(
    val meal: Meal,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : MealPlanEvent()

@Serializable
data class MealUpdatedEvent(
    val meal: Meal,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : MealPlanEvent()

@Serializable
data class MealRemovedEvent(
    val identity: MealIdentity,
    val strategy: DeleteStrategy,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : MealPlanEvent()

@Serializable
data class MealPlanReorderedEvent(
    val identities: List<MealIdentity>,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : MealPlanEvent()
