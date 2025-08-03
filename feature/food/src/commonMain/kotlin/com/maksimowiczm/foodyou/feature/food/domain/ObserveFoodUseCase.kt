package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import kotlinx.coroutines.flow.Flow
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
    private val productMapper: ProductMapper,
    private val observeRecipe: ObserveRecipeUseCase
) : ObserveFoodUseCase {
    private val productDao = foodDatabase.productDao

    override fun observe(foodId: FoodId): Flow<Food?> = when (foodId) {
        is FoodId.Product -> observe(foodId)
        is FoodId.Recipe -> observeRecipe(foodId)
    }

    override fun observe(foodId: FoodId.Product) = productDao
        .observe(foodId.id)
        .mapIfNotNull(productMapper::toModel)

    override fun observe(foodId: FoodId.Recipe) = observeRecipe(foodId)
}
