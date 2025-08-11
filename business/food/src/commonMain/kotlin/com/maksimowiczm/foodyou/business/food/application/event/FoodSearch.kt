package com.maksimowiczm.foodyou.business.food.application.event

import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.Event
import com.maksimowiczm.foodyou.shared.common.infrastructure.event.EventHandler
import kotlinx.datetime.LocalDateTime

data class FoodSearchEvent(val queryType: QueryType.NotBlank.Text, val date: LocalDateTime) : Event

internal class FoodSearchEventHandler(private val foodSearchSource: LocalFoodSearchDataSource) :
    EventHandler<FoodSearchEvent> {
    override suspend fun handle(event: FoodSearchEvent) {
        foodSearchSource.insertSearchHistory(event.toSearchHistory())
    }
}

private fun FoodSearchEvent.toSearchHistory(): SearchHistory {
    return SearchHistory(query = queryType.query, date = date)
}
