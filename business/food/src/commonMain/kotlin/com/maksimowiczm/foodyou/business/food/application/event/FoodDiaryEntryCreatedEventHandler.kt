package com.maksimowiczm.foodyou.business.food.application.event

import com.maksimowiczm.foodyou.business.food.domain.MeasurementSuggestionRepository
import com.maksimowiczm.foodyou.business.shared.application.event.EventHandler
import com.maksimowiczm.foodyou.business.shared.domain.fooddiary.FoodDiaryEntryCreatedDomainEvent

internal class FoodDiaryEntryCreatedEventHandler(
    private val measurementSuggestionRepository: MeasurementSuggestionRepository
) : EventHandler<FoodDiaryEntryCreatedDomainEvent> {
    override suspend fun handle(event: FoodDiaryEntryCreatedDomainEvent) {
        measurementSuggestionRepository.insert(
            foodId = event.foodId,
            measurement = event.measurement,
        )
    }
}
