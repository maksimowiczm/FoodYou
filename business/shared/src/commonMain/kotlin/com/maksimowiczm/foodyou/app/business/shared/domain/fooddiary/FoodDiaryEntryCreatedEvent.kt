package com.maksimowiczm.foodyou.app.business.shared.domain.fooddiary

import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.domain.event.DomainEvent
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import kotlinx.datetime.LocalDateTime

data class FoodDiaryEntryCreatedEvent(
    val foodId: FoodId,
    val date: LocalDateTime,
    val measurement: Measurement,
) : DomainEvent
