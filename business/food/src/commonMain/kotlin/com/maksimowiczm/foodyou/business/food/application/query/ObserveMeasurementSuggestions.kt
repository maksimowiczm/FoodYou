package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalMeasurementSuggestionDataSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

data class ObserveMeasurementSuggestionsQuery(val foodId: FoodId) : Query<List<Measurement>>

@OptIn(ExperimentalCoroutinesApi::class)
internal class ObserveMeasurementSuggestionsQueryHandler(
    private val queryBus: QueryBus,
    private val localSuggestions: LocalMeasurementSuggestionDataSource,
) : QueryHandler<ObserveMeasurementSuggestionsQuery, List<Measurement>> {
    override fun handle(query: ObserveMeasurementSuggestionsQuery): Flow<List<Measurement>> =
        queryBus.dispatch(ObserveFoodQuery(query.foodId)).flatMapLatest { food ->
            if (food == null) {
                flowOf(emptyList())
            } else {
                localSuggestions.observeByFoodId(query.foodId, limit = 5).map { list ->
                    list.fillMissingMeasurements(food)
                }
            }
        }
}

private fun List<Measurement>.fillMissingMeasurements(food: Food): List<Measurement> {
    val mutable = toMutableList()

    if (food.servingWeight != null) {
        mutable.add(Measurement.Serving(1.0))
    }

    if (food.totalWeight != null) {
        mutable.add(Measurement.Package(1.0))
    }

    if (food.isLiquid) {
        mutable.add(Measurement.Milliliter(100.0))
    } else {
        mutable.add(Measurement.Gram(100.0))
        mutable.add(Measurement.Ounce(3.0))
    }

    return mutable.distinct()
}
