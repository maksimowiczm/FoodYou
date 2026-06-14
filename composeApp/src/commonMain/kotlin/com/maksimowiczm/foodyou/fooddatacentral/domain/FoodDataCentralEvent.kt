package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class FoodDataCentralEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class FoodDataCentralProductUpdatedEvent(
    val product: FoodDataCentralProduct,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : FoodDataCentralEvent()
