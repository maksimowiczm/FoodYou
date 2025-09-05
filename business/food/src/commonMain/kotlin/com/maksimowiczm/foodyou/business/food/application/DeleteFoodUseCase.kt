package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeRepository
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first

sealed interface DeleteFoodError {
    data object FoodNotFound : DeleteFoodError
}

fun interface DeleteFoodUseCase {
    suspend fun deleteFood(foodId: FoodId): Result<Unit, DeleteFoodError>
}

internal class DeleteFoodUseCaseImpl(
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
    private val transactionProvider: TransactionProvider,
    private val logger: Logger,
) : DeleteFoodUseCase {
    override suspend fun deleteFood(foodId: FoodId): Result<Unit, DeleteFoodError> =
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
        const val TAG = "DeleteFoodUseCaseImpl"
    }
}
