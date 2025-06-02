package com.maksimowiczm.foodyou.core.domain.repository

import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun observeRecipe(recipeId: FoodId.Recipe): Flow<Recipe?>
}

// internal class RecipeRepositoryImpl(
//    private val recipeLocalDataSource: RecipeLocalDataSource,
//    private val productLocalDataSource: ProductLocalDataSource,
//    private val productMapper: ProductMapper = ProductMapper,
//    private val measurementMapper: MeasurementMapper = MeasurementMapper
// ) : RecipeRepository {
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    override fun observeRecipe(recipeId: FoodId.Recipe): Flow<Recipe?> {
//        val recipeFlow = recipeLocalDataSource.observeRecipe(recipeId.id)
//
//        val flow = recipeFlow.filterNotNull().flatMapLatest { entity ->
//            val (entity, ingredients) = entity
//
//            if (ingredients.isEmpty()) {
//                return@flatMapLatest flowOf(
//                    Recipe(
//                        id = FoodId.Recipe(entity.id),
//                        name = entity.name,
//                        servings = entity.servings,
//                        ingredients = emptyList()
//                    )
//                )
//            }
//
//            val flows = ingredients.mapNotNull { ingredient ->
//                when {
//                    ingredient.productId != null ->
//                        productLocalDataSource
//                            .observeProduct(ingredient.productId)
//                            .filterNotNull()
//                            .map {
//                                RecipeIngredient(
//                                    food = productMapper.toModel(it),
//                                    measurement = measurementMapper.toMeasurement(ingredient)
//                                )
//                            }
//
//                    ingredient.recipeIngredientId != null -> observeRecipe(
//                        FoodId.Recipe(ingredient.recipeIngredientId)
//                    )
//                        .filterNotNull()
//                        .map {
//                            RecipeIngredient(
//                                food = it,
//                                measurement = measurementMapper.toMeasurement(ingredient)
//                            )
//                        }
//
//                    else -> null
//                }
//            }
//
//            val recipe = combine(flows) { ingredientsList ->
//                Recipe(
//                    id = FoodId.Recipe(entity.id),
//                    name = entity.name,
//                    servings = entity.servings,
//                    ingredients = ingredientsList.toList()
//                )
//            }
//
//            recipe
//        }
//
//        return flow
//    }
// }
