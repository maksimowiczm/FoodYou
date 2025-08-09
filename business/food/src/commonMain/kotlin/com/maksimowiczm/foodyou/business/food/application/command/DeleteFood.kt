package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

data class DeleteFoodCommand(val foodId: FoodId.Product) : Command

sealed interface DeleteFoodError {
    data object FoodNotFound : DeleteFoodError

    data object UnknownError : DeleteFoodError
}

internal class DeleteFoodCommandHandler(
    private val localProductDataSource: LocalProductDataSource
) : CommandHandler<DeleteFoodCommand, Unit, DeleteFoodError> {

    override val commandType: KClass<DeleteFoodCommand> = DeleteFoodCommand::class

    override suspend fun handle(command: DeleteFoodCommand): Result<Unit, DeleteFoodError> {
        val (id) = command

        val product = localProductDataSource.observeProduct(id).first()

        if (product == null) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DeleteFoodError.FoodNotFound,
                message = { "Product with ID $id not found." },
            )
        }

        return try {
            localProductDataSource.deleteProduct(product)
            Ok(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = e,
                error = DeleteFoodError.UnknownError,
                message = { "Failed to delete product with ID $id: ${e.message}" },
            )
        }
    }

    private companion object {
        private const val TAG = "DeleteFoodCommandHandler"
    }
}
