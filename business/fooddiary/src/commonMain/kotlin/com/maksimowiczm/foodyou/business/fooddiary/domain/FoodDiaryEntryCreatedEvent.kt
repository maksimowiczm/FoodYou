package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.event.DomainEvent
import com.maksimowiczm.foodyou.shared.measurement.Measurement
import kotlinx.datetime.LocalDateTime

data class FoodDiaryEntryCreatedEvent(
    val foodId: FoodId,
    val date: LocalDateTime,
    val measurement: Measurement,
) : DomainEvent
