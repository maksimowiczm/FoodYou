package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.food.domain.repository.FoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.shared.event.EventHandler

internal class FoodDiaryEntryCreatedEventHandler(
    private val repository: FoodMeasurementSuggestionRepository
) : EventHandler<FoodDiaryEntryCreatedEvent> {
    override suspend fun handle(event: FoodDiaryEntryCreatedEvent) {
        repository.insert(foodId = event.foodId, measurement = event.measurement)
    }
}
