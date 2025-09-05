package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.food.domain.RecipeRepository
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

fun interface ObserveFoodUseCase {
    fun observe(foodId: FoodId): Flow<Food?>
}

internal class ObserveFoodUseCaseImpl(
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
) : ObserveFoodUseCase {
    override fun observe(foodId: FoodId): Flow<Food?> =
        when (foodId) {
            is FoodId.Product -> productRepository.observeProduct(foodId)
            is FoodId.Recipe -> recipeRepository.observeRecipe(foodId)
        }
}
