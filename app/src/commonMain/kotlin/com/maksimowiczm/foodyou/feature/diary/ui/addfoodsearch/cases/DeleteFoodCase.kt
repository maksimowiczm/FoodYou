package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.cases

import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.RecipeRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId

class DeleteFoodCase(
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(foodId: FoodId) {
        when (foodId) {
            is FoodId.Product -> productRepository.deleteProduct(foodId.productId)
            is FoodId.Recipe -> recipeRepository.deleteRecipe(foodId.recipeId)
        }
    }
}
