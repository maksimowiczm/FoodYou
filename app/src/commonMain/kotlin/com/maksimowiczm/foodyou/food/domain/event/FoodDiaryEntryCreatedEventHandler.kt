package com.maksimowiczm.foodyou.food.domain.event

import com.maksimowiczm.foodyou.common.domain.event.IntegrationEventHandler
import com.maksimowiczm.foodyou.food.domain.repository.FoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.event.FoodDiaryEntryCreatedEvent

internal class FoodDiaryEntryCreatedEventHandler(
    private val repository: FoodMeasurementSuggestionRepository
) : IntegrationEventHandler<FoodDiaryEntryCreatedEvent> {
    override suspend fun handle(event: FoodDiaryEntryCreatedEvent) {
        repository.insert(foodId = event.foodId, measurement = event.measurement)
    }
}
