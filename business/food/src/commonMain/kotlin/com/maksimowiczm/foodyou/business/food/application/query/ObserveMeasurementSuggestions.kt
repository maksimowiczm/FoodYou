package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.food.domain.possibleMeasurementTypes
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalMeasurementSuggestionDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.type
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
                localSuggestions.observeByFoodId(query.foodId, limit = 5).flatMapLatest { list ->
                    list.fillMissingMeasurements(food)
                }
            }
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun List<Measurement>.fillMissingMeasurements(food: Food): Flow<List<Measurement>> {
    val mutable = toMutableList()

    if (food.servingWeight != null) {
        mutable.add(Measurement.Serving(Measurement.Serving.DEFAULT))
    }

    if (food.totalWeight != null) {
        mutable.add(Measurement.Package(Measurement.Package.DEFAULT))
    }

    if (food.isLiquid) {
        mutable.add(Measurement.Milliliter(Measurement.Milliliter.DEFAULT))
        mutable.add(Measurement.FluidOunce(Measurement.FluidOunce.DEFAULT))
    } else {
        mutable.add(Measurement.Gram(Measurement.Gram.DEFAULT))
        mutable.add(Measurement.Ounce(Measurement.Ounce.DEFAULT))
    }

    return food.possibleMeasurementTypes.map { possible ->
        mutable.distinct().filter { it.type in possible }
    }
}
