package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.event.IntegrationEventHandler
import com.maksimowiczm.foodyou.food.domain.entity.FoodSearchHistory
import com.maksimowiczm.foodyou.food.domain.repository.FoodSearchHistoryRepository

class FoodSearchEventHandler(private val repository: FoodSearchHistoryRepository) :
    IntegrationEventHandler<FoodSearchEvent> {
    override suspend fun handle(event: FoodSearchEvent) {
        repository.insert(event.toSearchHistory())
    }
}

private fun FoodSearchEvent.toSearchHistory(): FoodSearchHistory =
    FoodSearchHistory(timestamp, query)
