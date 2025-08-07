package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.firstOrNull

data class DeleteMealCommand(val mealId: Long) : Command

sealed interface DeleteMealError {
    data object MealNotFound : DeleteMealError
}

internal class DeleteMealCommandHandler(private val mealDataSource: LocalMealDataSource) :
    CommandHandler<DeleteMealCommand, Unit, DeleteMealError> {
    override val commandType: KClass<DeleteMealCommand>
        get() = DeleteMealCommand::class

    override suspend fun handle(command: DeleteMealCommand): Result<Unit, DeleteMealError> {
        val meal = mealDataSource.observeMealById(command.mealId).firstOrNull()

        if (meal == null) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DeleteMealError.MealNotFound,
                message = { "Meal with ID ${command.mealId} not found." },
            )
        }

        mealDataSource.delete(meal)
        return Ok(Unit)
    }

    private companion object {
        private const val TAG = "DeleteMealCommandHandler"
    }
}
