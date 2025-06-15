package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.database.measurement.MeasurementLocalDataSource
import com.maksimowiczm.foodyou.core.database.search.FoodSearchLocalDataSource
import com.maksimowiczm.foodyou.core.domain.FoodRepository
import com.maksimowiczm.foodyou.core.domain.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.RecipeRepository
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Recipe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal fun interface QueryIngredientsUseCase {
    /**
     * Queries ingredients based on the provided query string and recipe ID.
     *
     * @param query search query
     * @param excludedRecipeId optional ID of the recipe to filter out possible circular references.
     *
     * Returns a flow of lists of [IngredientSearchItem] that match the query.
     */
    operator fun invoke(
        query: String?,
        excludedRecipeId: FoodId.Recipe?
    ): Flow<List<IngredientSearchItem>>
}

internal class QueryIngredientsUseCaseImpl(
    private val foodRepository: FoodRepository,
    private val foodSearchLocalDataSource: FoodSearchLocalDataSource,
    private val measurementLocalDataSource: MeasurementLocalDataSource,
    private val recipeRepository: RecipeRepository,
    private val measurementMapper: MeasurementMapper
) : QueryIngredientsUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(
        query: String?,
        excludedRecipeId: FoodId.Recipe?
    ): Flow<List<IngredientSearchItem>> {
        val barcode = query?.takeIf { it.all { it.isDigit() } }

        val queryFlow = if (barcode != null) {
            foodSearchLocalDataSource.queryFoodByBarcode(barcode = barcode, limit = 100)
        } else {
            foodSearchLocalDataSource.queryFood(query = query, limit = 100)
        }

        val result = queryFlow.flatMapLatest { searchList ->
            if (searchList.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            val itemFlows = searchList.mapNotNull { entity ->
                val productId = entity.productId
                val recipeId = entity.recipeId

                when {
                    productId != null -> mapToProduct(productId)
                    recipeId != null -> mapToRecipe(recipeId, excludedRecipeId)
                    else -> null
                }
            }

            combine(itemFlows) { it.filterNotNull().toList() }
        }

        return result
    }

    private fun mapToProduct(productId: Long): Flow<IngredientSearchItem> {
        val suggestionFlow = measurementLocalDataSource.observeAllMeasurementsByType(
            productId = productId
        ).map {
            it.firstOrNull()
        }

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

    private fun mapToRecipe(
        recipeId: Long,
        excludedRecipeId: FoodId.Recipe?
    ): Flow<IngredientSearchItem?> {
        val suggestionFlow = measurementLocalDataSource.observeAllMeasurementsByType(
            recipeId = recipeId
        ).map {
            it.firstOrNull()
        }

        val recipeFlow = recipeRepository.observeRecipe(FoodId.Recipe(recipeId)).filterNotNull()

        return combine(
            recipeFlow,
            suggestionFlow
        ) { recipe, suggestion ->

            if (recipe.id == excludedRecipeId) {
                return@combine null
            }

            val flatIngredients = recipe.flatIngredients()
            if (flatIngredients.any { it is Recipe && it.id == excludedRecipeId }) {
                return@combine null
            }

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
