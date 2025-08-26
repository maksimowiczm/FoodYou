package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.firstOrNull

data class DeleteMealCommand(val mealId: Long) : Command<Unit, DeleteMealError>

sealed interface DeleteMealError {
    data object MealNotFound : DeleteMealError
}

internal class DeleteMealCommandHandler(
    private val mealDataSource: LocalMealDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<DeleteMealCommand, Unit, DeleteMealError> {

    override suspend fun handle(command: DeleteMealCommand): Result<Unit, DeleteMealError> {
        val meal = mealDataSource.observeMealById(command.mealId).firstOrNull()

        if (meal == null) {
            return FoodYouLogger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DeleteMealError.MealNotFound,
                message = { "Meal with ID ${command.mealId} not found." },
            )
        }

        transactionProvider.withTransaction { mealDataSource.delete(meal) }

        return Ok(Unit)
    }

    private companion object {
        private const val TAG = "DeleteMealCommandHandler"
    }
}
