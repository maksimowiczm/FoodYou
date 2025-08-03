package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.Food
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ObserveMeasurementSuggestionsUseCase {

    /**
     * Observes measurement suggestions for a given food item.
     *
     * @param food The food item for which to observe measurement suggestions.
     * @param limit The maximum number of measurement suggestions to return.
     * @return A flow of measurement suggestions.
     */
    fun observe(food: Food, limit: Int): Flow<List<Measurement>>
}

internal class ObserveMeasurementSuggestionsUseCaseImpl(foodDiaryDatabase: FoodDiaryDatabase) :
    ObserveMeasurementSuggestionsUseCase {
    private val measurementDao = foodDiaryDatabase.measurementDao

    override fun observe(food: Food, limit: Int): Flow<List<Measurement>> =
        measurementDao.observeMeasurementSuggestions(
            productId = (food.id as? FoodId.Product)?.id,
            recipeId = (food.id as? FoodId.Recipe)?.id,
            limit = limit
        ).map { list ->
            val measurements = list.map { it.toMeasurement() }.filter {
                when (it) {
                    is Measurement.Gram, is Measurement.Milliliter -> true
                    is Measurement.Package -> food.totalWeight != null
                    is Measurement.Serving -> food.servingWeight != null
                }
            }.toMutableList()

            // Fill missing measurements
            if (food.isLiquid) {
                measurements.add(Measurement.Milliliter(100f))
            } else {
                measurements.add(Measurement.Gram(100f))
            }

            if (food.totalWeight != null) {
                measurements.add(Measurement.Package(1f))
            }
            if (food.servingWeight != null) {
                measurements.add(Measurement.Serving(1f))
            }

            measurements.distinct()
        }
}
