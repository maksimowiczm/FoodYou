package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.food.domain.entity.FoodSearchHistory
import com.maksimowiczm.foodyou.food.domain.repository.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.shared.event.EventHandler

class FoodSearchEventHandler(private val repository: FoodSearchHistoryRepository) :
    EventHandler<FoodSearchEvent> {
    override suspend fun handle(event: FoodSearchEvent) {
        repository.insert(event.toSearchHistory())
    }
}

private fun FoodSearchEvent.toSearchHistory(): FoodSearchHistory = FoodSearchHistory(date, query)
