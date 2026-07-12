package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class MealPlanEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class MealPlanInitializedEvent(
    val language: Language,
    val meals: List<Meal>,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : MealPlanEvent()

@Serializable
data class MealPlanUpdatedEvent(
    val meals: List<Meal>,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : MealPlanEvent()
