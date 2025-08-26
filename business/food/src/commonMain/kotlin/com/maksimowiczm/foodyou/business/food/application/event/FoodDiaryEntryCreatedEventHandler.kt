package com.maksimowiczm.foodyou.business.food.application.event

import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalMeasurementSuggestionDataSource
import com.maksimowiczm.foodyou.business.shared.application.event.EventHandler
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.fooddiary.FoodDiaryEntryCreatedDomainEvent

internal class FoodDiaryEntryCreatedEventHandler(
    private val localMeasurementSuggestion: LocalMeasurementSuggestionDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : EventHandler<FoodDiaryEntryCreatedDomainEvent> {
    override suspend fun handle(event: FoodDiaryEntryCreatedDomainEvent) {
        transactionProvider.withTransaction {
            localMeasurementSuggestion.insert(
                foodId = event.foodId,
                measurement = event.measurement,
            )
        }
    }
}
