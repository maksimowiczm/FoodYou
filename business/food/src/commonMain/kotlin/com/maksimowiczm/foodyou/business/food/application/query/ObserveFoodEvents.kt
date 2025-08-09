package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

data class ObserveFoodEventsQuery(val foodId: FoodId) : Query

internal class ObserveFoodEventsQueryHandler(private val localFoodEvent: LocalFoodEventDataSource) :
    QueryHandler<ObserveFoodEventsQuery, List<FoodEvent>> {
    override val queryType: KClass<ObserveFoodEventsQuery>
        get() = ObserveFoodEventsQuery::class

    override fun handle(query: ObserveFoodEventsQuery): Flow<List<FoodEvent>> {
        return localFoodEvent.observeFoodEvents(query.foodId)
    }
}
