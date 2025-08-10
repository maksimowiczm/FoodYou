package com.maksimowiczm.foodyou.business.shared.application.event

import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.Event
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.datetime.LocalDateTime

data class FoodDiaryEntryCreatedEvent(
    val foodId: FoodId,
    val entryId: Long,
    val date: LocalDateTime,
    val measurement: Measurement,
) : Event
