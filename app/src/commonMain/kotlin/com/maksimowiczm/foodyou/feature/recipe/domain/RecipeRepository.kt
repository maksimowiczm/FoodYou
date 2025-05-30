package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity
import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.mapper.RecipeMapper
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.core.domain.source.FoodLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.ProductMeasurementLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.RecipeLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.RecipeMeasurementLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

internal class RecipeRepository(
    private val foodRepository: FoodRepository,
    private val foodLocalDataSource: FoodLocalDataSource,
    private val recipeLocalDataSource: RecipeLocalDataSource,
    private val productMeasurementLocalDataSource: ProductMeasurementLocalDataSource,
    private val recipeMeasurementLocalDataSource: RecipeMeasurementLocalDataSource,
    private val measurementMapper: MeasurementMapper = MeasurementMapper,
    private val recipeMapper: RecipeMapper = RecipeMapper
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun queryIngredients(query: String?): Flow<List<IngredientSearchItem>> {
        val barcode = query?.takeIf { it.all { it.isDigit() } }

        val queryFlow = if (barcode != null) {
            foodLocalDataSource.queryFoodByBarcode(barcode)
        } else {
            foodLocalDataSource.queryFood(query)
        }

        return queryFlow.flatMapLatest { searchList ->
            if (searchList.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            val itemFlows = searchList.mapNotNull { entity ->
                return@mapNotNull when {
                    entity.productId != null -> mapToProduct(entity)
                    entity.recipeId != null -> mapToRecipe(entity)
                    else -> null
                }
            }

            combine(itemFlows) { it.toList() }
        }
    }

    private fun mapToProduct(entity: FoodSearchEntity): Flow<IngredientSearchItem>? {
        val productId = entity.productId ?: return null

        val suggestionFlow =
            productMeasurementLocalDataSource.observeLatestProductMeasurementSuggestion(
                productId = productId
            )

        val productFlow = foodRepository.observeFood(FoodId.Product(productId)).filterNotNull()

        return combine(
            productFlow,
            suggestionFlow
        ) { product, suggestion ->
            val measurement = suggestion
                ?.let { measurementMapper.toMeasurement(suggestion) }
                ?: Measurement.defaultForFood(product)

            IngredientSearchItem(
                food = product,
                measurement = measurement,
                uniqueId = product.id.toString()
            )
        }
    }

    private fun mapToRecipe(entity: FoodSearchEntity): Flow<IngredientSearchItem>? {
        val recipeId = entity.recipeId ?: return null

        val suggestionFlow =
            recipeMeasurementLocalDataSource.observeLatestRecipeMeasurementSuggestion(
                recipeId = recipeId
            )

        val recipeFlow = recipeLocalDataSource.observeRecipe(recipeId).filterNotNull()

        return combine(
            recipeFlow,
            suggestionFlow
        ) { recipe, suggestion ->
            val recipe = recipeMapper.toModel(recipe)

            val measurement = suggestion
                ?.let { measurementMapper.toMeasurement(suggestion) }
                ?: Measurement.defaultForFood(recipe)

            IngredientSearchItem(
                food = recipe,
                measurement = measurement,
                uniqueId = recipe.id.toString()
            )
        }
    }
}
