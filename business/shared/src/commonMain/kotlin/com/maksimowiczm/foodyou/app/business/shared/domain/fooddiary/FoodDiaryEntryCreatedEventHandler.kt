package com.maksimowiczm.foodyou.app.business.shared.domain.fooddiary

import com.maksimowiczm.foodyou.food.domain.repository.FoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.shared.domain.event.EventHandler

internal class FoodDiaryEntryCreatedEventHandler(
    private val repository: FoodMeasurementSuggestionRepository
) : EventHandler<FoodDiaryEntryCreatedEvent> {
    override suspend fun handle(event: FoodDiaryEntryCreatedEvent) {
        repository.insert(foodId = event.foodId, measurement = event.measurement)
    }
}
