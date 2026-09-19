package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.Serializable

@Serializable sealed interface FoodDataCentralEvent : DomainEvent

@Serializable
data class FoodDataCentralProductUpdatedEvent(val product: FoodDataCentralProduct) :
    FoodDataCentralEvent
