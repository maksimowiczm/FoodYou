package com.maksimowiczm.foodyou.feature.diary.ui.measurement.cases

import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.RecipeRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementSuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.model.Food
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveProductCase(
    private val productRepository: ProductRepository,
    private val measurementRepository: MeasurementRepository,
    private val recipeRepository: RecipeRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(foodId: FoodId): Flow<Food?> = when (foodId) {
        is FoodId.Recipe -> recipeRepository.observeRecipeById(foodId.recipeId)
            .filterNotNull()
            .flatMapLatest { recipe ->
                measurementRepository
                    .observeMeasurementSuggestionByFood(foodId)
                    .map { suggestions ->
                        Food(
                            id = recipe.id,
                            name = recipe.name,
                            nutrients = recipe.nutrients,
                            suggestion = suggestions,
                            packageWeight = recipe.packageWeight,
                            servingWeight = recipe.servingWeight,
                            highlight = null
                        )
                    }
            }

        is FoodId.Product -> productRepository.observeProductById(foodId.productId)
            .filterNotNull()
            .flatMapLatest { product ->
                measurementRepository
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
        return measurementRepository
            .observeMeasurementById(measurementId)
            .flatMapLatest { measurement ->
                if (measurement == null) {
                    return@flatMapLatest flowOf(null)
                }

                val packageSuggestion = measurement.measurement as? WeightMeasurement.Package
                val servingSuggestion = measurement.measurement as? WeightMeasurement.Serving
                val weightSuggestion = measurement.measurement as? WeightMeasurement.WeightUnit

                val foodFlow = when (val foodId = measurement.foodId) {
                    is FoodId.Recipe -> recipeRepository.observeRecipeById(foodId.recipeId)
                    is FoodId.Product -> productRepository.observeProductById(foodId.productId)
                }

                combine(
                    measurementRepository.observeMeasurementSuggestionByFood(measurement.foodId),
                    foodFlow
                ) { suggestion, product ->
                    if (product == null) {
                        return@combine null
                    }

                    val suggestion = MeasurementSuggestion(
                        packageSuggestion = packageSuggestion ?: suggestion.packageSuggestion,
                        servingSuggestion = servingSuggestion ?: suggestion.servingSuggestion,
                        weightSuggestion = weightSuggestion ?: suggestion.weightSuggestion
                    )

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
