package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.food.ProductLocalDataSource
import com.maksimowiczm.foodyou.core.database.food.RecipeLocalDataSource
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Recipe
import com.maksimowiczm.foodyou.core.model.RecipeIngredient
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
    private val productMapper: ProductMapper,
    private val measurementMapper: MeasurementMapper
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
                        ingredients = emptyList(),
                        isLiquid = entity.isLiquid,
                        notes = entity.note
                    )
                )
            }

            val flows = ingredients.mapNotNull { ingredient ->
                val ingredientProductId = ingredient.ingredientProductId
                val recipeIngredientId = ingredient.ingredientRecipeId

                when {
                    ingredientProductId != null ->
                        productLocalDataSource
                            .observeProductById(ingredientProductId)
                            .filterNotNull()
                            .map {
                                RecipeIngredient(
                                    food = productMapper.toModel(it),
                                    measurement = measurementMapper.toMeasurement(ingredient)
                                )
                            }

                    recipeIngredientId != null -> observeRecipe(FoodId.Recipe(recipeIngredientId))
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
                    ingredients = ingredientsList.toList(),
                    isLiquid = entity.isLiquid,
                    notes = entity.note
                )
            }

            recipe
        }

        return flow
    }
}
