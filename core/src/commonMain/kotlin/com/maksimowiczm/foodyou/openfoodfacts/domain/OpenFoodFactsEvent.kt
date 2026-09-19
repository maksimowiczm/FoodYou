package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.Serializable

@Serializable sealed interface OpenFoodFactsEvent : DomainEvent

@Serializable
data class OpenFoodFactsProductUpdatedEvent(val product: OpenFoodFactsProduct) : OpenFoodFactsEvent
