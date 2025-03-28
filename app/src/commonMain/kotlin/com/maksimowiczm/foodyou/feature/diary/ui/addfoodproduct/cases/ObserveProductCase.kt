package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases

import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveQuantitySuggestionByProductId
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.model.Product
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveProductCase(
    private val productRepository: ProductRepository,
    private val observeQuantitySuggestionByProductId: ObserveQuantitySuggestionByProductId,
    private val measurementRepository: MeasurementRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(productId: Long): Flow<Product?> {
        return productRepository.observeProductById(productId).flatMapLatest { product ->
            if (product == null) {
                return@flatMapLatest flowOf(null)
            }

            observeQuantitySuggestionByProductId
                .observeQuantitySuggestionByProductId(productId)
                .map { suggestions ->
                    val packageSuggestion = suggestions.singleOrNull {
                        it is WeightMeasurement.Package
                    } as? WeightMeasurement.Package

                    val servingSuggestion = suggestions.singleOrNull {
                        it is WeightMeasurement.Serving
                    } as? WeightMeasurement.Serving

                    val weightSuggestion = suggestions.singleOrNull {
                        it is WeightMeasurement.WeightUnit
                    } as? WeightMeasurement.WeightUnit ?: WeightMeasurement.WeightUnit(100f)

                    Product(
                        id = product.id,
                        name = product.name,
                        nutrients = product.nutrients,
                        packageSuggestion = packageSuggestion,
                        servingSuggestion = servingSuggestion,
                        weightSuggestions = weightSuggestion,
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

                val packageSuggestion = measurement.measurement as? WeightMeasurement.Package
                val servingSuggestion = measurement.measurement as? WeightMeasurement.Serving
                val weightSuggestion = measurement.measurement as? WeightMeasurement.WeightUnit

                observeQuantitySuggestionByProductId
                    .observeQuantitySuggestionByProductId(measurement.product.id)
                    .map { suggestions ->
                        val packageSuggestion = packageSuggestion
                            ?: suggestions.singleOrNull {
                                it is WeightMeasurement.Package
                            } as? WeightMeasurement.Package

                        val servingSuggestion = servingSuggestion
                            ?: suggestions.singleOrNull {
                                it is WeightMeasurement.Serving
                            } as? WeightMeasurement.Serving

                        val weightSuggestion = weightSuggestion
                            ?: suggestions.singleOrNull {
                                it is WeightMeasurement.WeightUnit
                            } as? WeightMeasurement.WeightUnit
                            ?: WeightMeasurement.WeightUnit(100f)

                        Product(
                            id = measurement.product.id,
                            name = measurement.product.name,
                            nutrients = measurement.product.nutrients,
                            packageSuggestion = packageSuggestion,
                            servingSuggestion = servingSuggestion,
                            weightSuggestions = weightSuggestion,
                            packageWeight = measurement.product.packageWeight,
                            servingWeight = measurement.product.servingWeight,
                            highlight = measurement.measurement.asEnum()
                        )
                    }
            }
    }
}
