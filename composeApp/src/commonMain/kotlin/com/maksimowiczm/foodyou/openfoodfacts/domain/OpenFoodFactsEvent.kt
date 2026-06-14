package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class OpenFoodFactsEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class OpenFoodFactsProductUpdatedEvent(
    val product: OpenFoodFactsProduct,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : OpenFoodFactsEvent()
