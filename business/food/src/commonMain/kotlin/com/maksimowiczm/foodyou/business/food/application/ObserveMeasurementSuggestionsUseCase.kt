package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.food.domain.MeasurementSuggestionRepository
import com.maksimowiczm.foodyou.business.food.domain.possibleMeasurementTypes
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.domain.measurement.type
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun interface ObserveMeasurementSuggestionsUseCase {
    fun observe(foodId: FoodId): Flow<List<Measurement>>
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class ObserveMeasurementSuggestionsUseCaseImpl(
    private val observeFoodUseCase: ObserveFoodUseCase,
    private val repository: MeasurementSuggestionRepository,
) : ObserveMeasurementSuggestionsUseCase {
    override fun observe(foodId: FoodId): Flow<List<Measurement>> =
        observeFoodUseCase.observe(foodId).flatMapLatest { food ->
            if (food == null) {
                flowOf(emptyList())
            } else {
                repository.observeByFoodId(foodId, limit = 5).flatMapLatest { list ->
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
