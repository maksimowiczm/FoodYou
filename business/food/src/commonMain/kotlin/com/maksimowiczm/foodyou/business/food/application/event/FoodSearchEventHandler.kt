package com.maksimowiczm.foodyou.business.food.application.event

import com.maksimowiczm.foodyou.business.food.domain.FoodSearchDomainEvent
import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.food.domain.SearchHistoryRepository
import com.maksimowiczm.foodyou.business.shared.application.event.EventHandler

internal class FoodSearchEventHandler(private val repository: SearchHistoryRepository) :
    EventHandler<FoodSearchDomainEvent> {
    override suspend fun handle(event: FoodSearchDomainEvent) {
        repository.insertSearchHistory(event.toSearchHistory())
    }
}

private fun FoodSearchDomainEvent.toSearchHistory(): SearchHistory {
    return SearchHistory(query = queryType.query, date = date)
}
