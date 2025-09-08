package com.maksimowiczm.foodyou.food.domain.usecase

import com.maksimowiczm.foodyou.food.domain.entity.Food
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.food.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class ObserveFoodUseCase(
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
) {
    fun observe(foodId: FoodId): Flow<Food?> =
        when (foodId) {
            is FoodId.Product -> productRepository.observeProduct(foodId)
            is FoodId.Recipe -> recipeRepository.observeRecipe(foodId)
        }
}
