package com.maksimowiczm.foodyou.feature.diary.ui.measurement.cases

import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementSuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.model.Food
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveProductCase(
    private val productRepository: ProductRepository,
    private val measurementsRepository: MeasurementRepository,
    private val measurementRepository: MeasurementRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(foodId: FoodId): Flow<Food?> {
        // TODO

        foodId as? FoodId.Product ?: return flowOf(null)

        return productRepository.observeProductById(foodId.productId).flatMapLatest { product ->
            if (product == null) {
                return@flatMapLatest flowOf(null)
            }

            measurementsRepository
                .observeMeasurementSuggestionByFood(foodId)
                .map { suggestions ->
                    Food(
                        id = product.id,
                        name = product.name,
                        nutrients = product.nutrients,
                        suggestion = suggestions,
                        packageWeight = product.packageWeight,
                        servingWeight = product.servingWeight,
                        highlight = null
                    )
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(measurementId: MeasurementId): Flow<Food?> {
        measurementId as MeasurementId.Product

        return measurementRepository
            .observeMeasurementById(measurementId)
            .flatMapLatest { measurement ->
                if (measurement == null) {
                    return@flatMapLatest flowOf(null)
                }

                val foodId = measurement.foodId as? FoodId.Product
                if (foodId == null) {
                    return@flatMapLatest flowOf(null)
                }

                val packageSuggestion = measurement.measurement as? WeightMeasurement.Package
                val servingSuggestion = measurement.measurement as? WeightMeasurement.Serving
                val weightSuggestion = measurement.measurement as? WeightMeasurement.WeightUnit

                combine(
                    measurementRepository.observeMeasurementSuggestionByFood(measurement.foodId),
                    productRepository.observeProductById(foodId.productId)
                ) { suggestion, product ->
                    if (product == null) {
                        return@combine null
                    }

                    val packageSuggestion = packageSuggestion ?: suggestion.packageSuggestion
                    val servingSuggestion = servingSuggestion ?: suggestion.servingSuggestion
                    val weightSuggestion = weightSuggestion ?: suggestion.weightSuggestion

                    val suggestion = object : MeasurementSuggestion {
                        override val packageSuggestion = packageSuggestion
                        override val servingSuggestion = servingSuggestion
                        override val weightSuggestion = weightSuggestion
                    }

                    Food(
                        id = product.id,
                        name = product.name,
                        nutrients = product.nutrients,
                        suggestion = suggestion,
                        packageWeight = product.packageWeight,
                        servingWeight = product.servingWeight,
                        highlight = measurement.measurement.asEnum()
                    )
                }
            }
    }
}
