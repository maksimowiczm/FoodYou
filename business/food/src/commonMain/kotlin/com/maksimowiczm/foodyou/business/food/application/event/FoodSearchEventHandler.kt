package com.maksimowiczm.foodyou.business.food.application.event

import com.maksimowiczm.foodyou.business.food.domain.FoodSearchDomainEvent
import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.business.shared.application.event.EventHandler

internal class FoodSearchEventHandler(private val foodSearchSource: LocalFoodSearchDataSource) :
    EventHandler<FoodSearchDomainEvent> {
    override suspend fun handle(event: FoodSearchDomainEvent) {
        foodSearchSource.insertSearchHistory(event.toSearchHistory())
    }
}

private fun FoodSearchDomainEvent.toSearchHistory(): SearchHistory {
    return SearchHistory(query = queryType.query, date = date)
}
