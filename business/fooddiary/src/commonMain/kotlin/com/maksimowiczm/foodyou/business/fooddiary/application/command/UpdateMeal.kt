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
import kotlinx.datetime.LocalTime

data class UpdateMealCommand(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
) : Command<Unit, UpdateMealError>

sealed interface UpdateMealError {
    data object MealNotFound : UpdateMealError

    data object InvalidName : UpdateMealError

    data object InvalidTimeRange : UpdateMealError
}

internal class UpdateMealCommandHandler(
    private val mealDataSource: LocalMealDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<UpdateMealCommand, Unit, UpdateMealError> {

    override suspend fun handle(command: UpdateMealCommand): Result<Unit, UpdateMealError> {
        if (command.name.isBlank()) {
            return FoodYouLogger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateMealError.InvalidName,
                message = { "Meal name cannot be blank." },
            )
        }

        if (command.from > command.to) {
            return FoodYouLogger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateMealError.InvalidTimeRange,
                message = { "Meal 'from' time must be before or equal to 'to' time." },
            )
        }

        val meal = mealDataSource.observeMealById(command.id).firstOrNull()

        if (meal == null) {
            return FoodYouLogger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateMealError.MealNotFound,
                message = { "Meal with ID ${command.id} not found." },
            )
        }

        val updatedMeal = meal.copy(name = command.name, from = command.from, to = command.to)

        transactionProvider.withTransaction { mealDataSource.update(updatedMeal) }

        return Ok(Unit)
    }

    private companion object {
        const val TAG = "UpdateMealCommandHandler"
    }
}
