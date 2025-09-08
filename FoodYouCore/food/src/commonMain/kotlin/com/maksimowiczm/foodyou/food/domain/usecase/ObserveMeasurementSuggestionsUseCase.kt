package com.maksimowiczm.foodyou.food.domain.usecase

import com.maksimowiczm.foodyou.food.domain.entity.Food
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.repository.FoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.domain.measurement.MeasurementType
import com.maksimowiczm.foodyou.shared.domain.measurement.type
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveMeasurementSuggestionsUseCase(
    private val observeFoodUseCase: ObserveFoodUseCase,
    private val repository: FoodMeasurementSuggestionRepository,
) {
    fun observe(foodId: FoodId, limit: Int): Flow<List<Measurement>> =
        observeFoodUseCase.observe(foodId).flatMapLatest { food ->
            if (food == null) {
                flowOf(emptyList())
            } else {
                repository.observeByFoodId(foodId = foodId, limit = limit).map { list ->
                    list.fillMissingMeasurements(food)
                }
            }
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun List<Measurement>.fillMissingMeasurements(food: Food): List<Measurement> {
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

    return mutable.distinct().filter { it.type in food.possibleMeasurementTypes }
}

private val Food.possibleMeasurementTypes: List<MeasurementType>
    get() =
        MeasurementType.entries.filter { type ->
            when (type) {
                MeasurementType.Gram -> !isLiquid
                MeasurementType.Ounce -> !isLiquid
                MeasurementType.Milliliter -> isLiquid
                MeasurementType.FluidOunce -> isLiquid
                MeasurementType.Package -> totalWeight != null
                MeasurementType.Serving -> servingWeight != null
            }
        }
