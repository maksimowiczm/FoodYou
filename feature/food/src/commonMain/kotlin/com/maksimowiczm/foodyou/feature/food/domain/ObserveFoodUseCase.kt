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
import kotlinx.coroutines.flow.mapIfNotNull

interface ObserveFoodUseCase {
    /**
     * Observes a food item by its ID.
     *
     * @param foodId The ID of the food item to observe.
     * @return A flow that emits the food item when it changes.
     */
    fun observe(foodId: FoodId): Flow<Food?>

    fun observe(foodId: FoodId.Product): Flow<Product?>

    fun observe(foodId: FoodId.Recipe): Flow<Recipe?>
}

internal class ObserveFoodUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val productMapper: ProductMapper
) : ObserveFoodUseCase {
    private val productDao = foodDatabase.productDao
    private val recipeDao = foodDatabase.recipeDao

    override fun observe(foodId: FoodId): Flow<Food?> = when (foodId) {
        is FoodId.Product -> observe(foodId)
        is FoodId.Recipe -> observe(foodId)
    }

    override fun observe(foodId: FoodId.Product) = productDao
        .observe(foodId.id)
        .mapIfNotNull(productMapper::toModel)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observe(foodId: FoodId.Recipe): Flow<Recipe?> = recipeDao
        .observeWithIngredients(foodId.id)
        .flatMapLatest { entity ->
            if (entity == null) {
                return@flatMapLatest flowOf(null)
            }

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
}
