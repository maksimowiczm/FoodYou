package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapIfNotNull

fun interface ObserveFoodUseCase {
    /**
     * Observes a food item by its ID.
     *
     * @param foodId The ID of the food item to observe.
     * @return A flow that emits the food item when it changes.
     */
    fun observe(foodId: FoodId): Flow<Food?>

    operator fun invoke(foodId: FoodId): Flow<Food?> = observe(foodId)
}

internal class ObserveFoodUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val productMapper: ProductMapper,
    private val observeRecipe: ObserveRecipeUseCase
) : ObserveFoodUseCase {
    private val productDao = foodDatabase.productDao

    override fun observe(foodId: FoodId): Flow<Food?> = when (foodId) {
        is FoodId.Product ->
            productDao
                .observe(foodId.id)
                .mapIfNotNull(productMapper::toModel)

        is FoodId.Recipe -> observeRecipe(foodId)
    }
}
