package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

data class ObserveFoodEventsQuery(val foodId: FoodId) : Query<List<FoodEvent>>

internal class ObserveFoodEventsQueryHandler(private val localFoodEvent: LocalFoodEventDataSource) :
    QueryHandler<ObserveFoodEventsQuery, List<FoodEvent>> {
    override fun handle(query: ObserveFoodEventsQuery): Flow<List<FoodEvent>> {
        return localFoodEvent.observeFoodEvents(query.foodId)
    }
}
