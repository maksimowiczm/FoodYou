package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.from
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun interface ObserveRecipeUseCase {
    fun observe(recipeId: FoodId.Recipe): Flow<Recipe?>
    operator fun invoke(recipeId: FoodId.Recipe): Flow<Recipe?> = observe(recipeId)
}

internal class ObserveRecipeUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val productMapper: ProductMapper
) : ObserveRecipeUseCase {
    private val recipeDao = foodDatabase.recipeDao
    private val productDao = foodDatabase.productDao

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observe(recipeId: FoodId.Recipe): Flow<Recipe?> {
        val recipeFlow = recipeDao.observeWithIngredients(recipeId.id)

        val flow = recipeFlow.filterNotNull().flatMapLatest { entity ->
            val (entity, ingredients) = entity

            if (ingredients.isEmpty()) {
                return@flatMapLatest flowOf(
                    Recipe(
                        id = FoodId.Recipe(entity.id),
                        name = entity.name,
                        servings = entity.servings,
                        ingredients = emptyList(),
                        note = entity.note,
                        isLiquid = entity.isLiquid
                    )
                )
            }

            val flows = ingredients.mapNotNull { ingredient ->
                val ingredientProductId = ingredient.ingredientProductId
                val recipeIngredientId = ingredient.ingredientRecipeId

                when {
                    ingredientProductId != null ->
                        productDao.observe(ingredientProductId)
                            .filterNotNull()
                            .map {
                                RecipeIngredient(
                                    food = productMapper.toModel(it),
                                    measurement = Measurement.from(
                                        type = ingredient.measurement,
                                        rawValue = ingredient.quantity
                                    )
                                )
                            }

                    recipeIngredientId != null -> observe(
                        FoodId.Recipe(ingredient.ingredientRecipeId)
                    )
                        .filterNotNull()
                        .map { recipe ->
                            RecipeIngredient(
                                food = recipe,
                                measurement = Measurement.from(
                                    type = ingredient.measurement,
                                    rawValue = ingredient.quantity
                                )
                            )
                        }

                    else -> null
                }
            }

            combine(flows) { ingredientsList ->
                Recipe(
                    id = FoodId.Recipe(entity.id),
                    name = entity.name,
                    servings = entity.servings,
                    ingredients = ingredientsList.toList(),
                    note = entity.note,
                    isLiquid = entity.isLiquid
                )
            }
        }

        return flow
    }
}
