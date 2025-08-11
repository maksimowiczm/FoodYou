package com.maksimowiczm.foodyou.business.food.application.event

import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalMeasurementSuggestionDataSource
import com.maksimowiczm.foodyou.business.shared.application.event.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.infrastructure.event.EventHandler

internal class FoodDiaryEntryCreatedEventHandler(
    private val localMeasurementSuggestion: LocalMeasurementSuggestionDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : EventHandler<FoodDiaryEntryCreatedEvent> {
    override suspend fun handle(event: FoodDiaryEntryCreatedEvent) {
        transactionProvider.withTransaction {
            localMeasurementSuggestion.insert(
                foodId = event.foodId,
                measurement = event.measurement,
            )
        }
    }
}
