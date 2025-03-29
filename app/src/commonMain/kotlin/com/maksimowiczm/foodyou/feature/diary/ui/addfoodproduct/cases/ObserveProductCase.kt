package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases

import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.model.Product
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
    operator fun invoke(productId: Long): Flow<Product?> {
        return productRepository.observeProductById(productId).flatMapLatest { product ->
            if (product == null) {
                return@flatMapLatest flowOf(null)
            }

            measurementsRepository
                .observeMeasurementSuggestionByFood(FoodId.Product(productId))
                .map { suggestions ->
                    Product(
                        id = product.id,
                        name = product.name,
                        nutrients = product.nutrients,
                        packageSuggestion = suggestions.packageSuggestion,
                        servingSuggestion = suggestions.servingSuggestion,
                        weightSuggestions = suggestions.weightSuggestion,
                        packageWeight = product.packageWeight,
                        servingWeight = product.servingWeight,
                        highlight = null
                    )
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(measurementId: MeasurementId): Flow<Product?> {
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
                ) { suggestions, product ->
                    if (product == null) {
                        return@combine null
                    }

                    val packageSuggestion = packageSuggestion ?: suggestions.packageSuggestion
                    val servingSuggestion = servingSuggestion ?: suggestions.servingSuggestion
                    val weightSuggestion = weightSuggestion ?: suggestions.weightSuggestion

                    Product(
                        id = product.id,
                        name = product.name,
                        nutrients = product.nutrients,
                        packageSuggestion = packageSuggestion,
                        servingSuggestion = servingSuggestion,
                        weightSuggestions = weightSuggestion,
                        packageWeight = product.packageWeight,
                        servingWeight = product.servingWeight,
                        highlight = measurement.measurement.asEnum()
                    )
                }
            }
    }
}
