package com.maksimowiczm.foodyou.core.domain.repository

import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Recipe
import com.maksimowiczm.foodyou.core.domain.model.RecipeIngredient
import com.maksimowiczm.foodyou.core.domain.source.ProductLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.RecipeLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface RecipeRepository {
    fun observeRecipe(recipeId: FoodId.Recipe): Flow<Recipe?>
}

internal class RecipeRepositoryImpl(
    private val recipeLocalDataSource: RecipeLocalDataSource,
    private val productLocalDataSource: ProductLocalDataSource,
    private val productMapper: ProductMapper = ProductMapper,
    private val measurementMapper: MeasurementMapper = MeasurementMapper
) : RecipeRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeRecipe(recipeId: FoodId.Recipe): Flow<Recipe?> {
        val recipeFlow = recipeLocalDataSource.observeRecipe(recipeId.id)

        val flow = recipeFlow.filterNotNull().flatMapLatest { entity ->
            val (entity, ingredients) = entity

            if (ingredients.isEmpty()) {
                return@flatMapLatest flowOf(
                    Recipe(
                        id = FoodId.Recipe(entity.id),
                        name = entity.name,
                        servings = entity.servings,
                        ingredients = emptyList()
                    )
                )
            }

            val flows = ingredients.mapNotNull { ingredient ->
                when {
                    ingredient.productId != null ->
                        productLocalDataSource
                            .observeProduct(ingredient.productId)
                            .filterNotNull()
                            .map {
                                RecipeIngredient(
                                    food = productMapper.toModel(it),
                                    measurement = measurementMapper.toMeasurement(ingredient)
                                )
                            }

                    ingredient.recipeIngredientId != null -> observeRecipe(
                        FoodId.Recipe(ingredient.recipeIngredientId)
                    )
                        .filterNotNull()
                        .map {
                            RecipeIngredient(
                                food = it,
                                measurement = measurementMapper.toMeasurement(ingredient)
                            )
                        }

                    else -> null
                }
            }

            val recipe = combine(flows) { ingredientsList ->
                Recipe(
                    id = FoodId.Recipe(entity.id),
                    name = entity.name,
                    servings = entity.servings,
                    ingredients = ingredientsList.toList()
                )
            }

            recipe
        }

        return flow
    }
}
