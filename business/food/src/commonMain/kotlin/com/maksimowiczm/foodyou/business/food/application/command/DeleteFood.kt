package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first

data class DeleteFoodCommand(val foodId: FoodId) : Command<Unit, DeleteFoodError> {}

sealed interface DeleteFoodError {
    data object FoodNotFound : DeleteFoodError
}

internal class DeleteFoodCommandHandler(
    private val localProductDataSource: LocalProductDataSource,
    private val localRecipe: LocalRecipeDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<DeleteFoodCommand, Unit, DeleteFoodError> {

    override suspend fun handle(command: DeleteFoodCommand): Result<Unit, DeleteFoodError> {
        val (id) = command

        val food =
            when (id) {
                is FoodId.Product -> localProductDataSource.observeProduct(id)
                is FoodId.Recipe -> localRecipe.observeRecipe(id)
            }.first()

        if (food == null) {
            return FoodYouLogger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DeleteFoodError.FoodNotFound,
                message = { "Food with ID $id not found." },
            )
        }

        transactionProvider.withTransaction {
            when (food) {
                is Product -> localProductDataSource.deleteProduct(food)
                is Recipe -> localRecipe.deleteRecipe(food)
            }
        }

        return Ok(Unit)
    }

    private companion object {
        private const val TAG = "DeleteFoodCommandHandler"
    }
}
