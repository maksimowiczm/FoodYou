package com.maksimowiczm.foodyou.business.shared.domain.fooddiary

import com.maksimowiczm.foodyou.business.shared.domain.event.DomainEvent
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.datetime.LocalDateTime

data class FoodDiaryEntryCreatedDomainEvent(
    val foodId: FoodId,
    val date: LocalDateTime,
    val measurement: Measurement,
) : DomainEvent
