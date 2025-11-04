package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.event.IntegrationEvent

data class LocalFoodDeletedEvent(val identity: FoodProductIdentity.Local) : IntegrationEvent
