package com.maksimowiczm.foodyou.food.domain.usecase

import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.entity.Product
import com.maksimowiczm.foodyou.food.domain.entity.Recipe
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.food.domain.repository.RecipeRepository
import com.maksimowiczm.foodyou.shared.domain.Ok
import com.maksimowiczm.foodyou.shared.domain.Result
import com.maksimowiczm.foodyou.shared.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import com.maksimowiczm.foodyou.shared.domain.log.logAndReturnFailure
import kotlinx.coroutines.flow.first

sealed interface DeleteFoodError {
    data object FoodNotFound : DeleteFoodError
}

class DeleteFoodUseCase(
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
    private val transactionProvider: TransactionProvider,
    private val logger: Logger,
) {
    suspend fun delete(foodId: FoodId): Result<Unit, DeleteFoodError> =
        transactionProvider.withTransaction {
            val food =
                when (foodId) {
                    is FoodId.Product -> productRepository.observeProduct(foodId)
                    is FoodId.Recipe -> recipeRepository.observeRecipe(foodId)
                }.first()

            if (food == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = DeleteFoodError.FoodNotFound,
                    message = { "Food with ID $foodId not found." },
                )
            }

            when (food) {
                is Product -> productRepository.deleteProduct(food)
                is Recipe -> recipeRepository.deleteRecipe(food)
            }

            Ok(Unit)
        }

    private companion object {
        const val TAG = "DeleteFoodUseCase"
    }
}
